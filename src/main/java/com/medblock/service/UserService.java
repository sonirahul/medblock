package com.medblock.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.medblock.config.Constants;
import com.medblock.config.FileStorageProperties;
import com.medblock.domain.Authority;
import com.medblock.domain.FileAssets;
import com.medblock.domain.User;
import com.medblock.repository.AuthorityRepository;
import com.medblock.repository.FileAssetsRepository;
import com.medblock.repository.UserRepository;
import com.medblock.security.AuthoritiesConstants;
import com.medblock.security.SecurityUtils;
import com.medblock.service.dto.UserDTO;
import com.medblock.service.util.RandomUtil;
import com.medblock.web.rest.errors.*;

import com.medblock.web.rest.vm.*;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final Path fileStorageLocation;

    @Autowired
    private FileAssetsRepository fileAssetsRepository;

    @Autowired
    private Session session;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthorityRepository authorityRepository, CacheManager cacheManager,
                       FileStorageProperties fileStorageProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;

        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
            .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                userRepository.save(user);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                userRepository.save(user);
                this.clearUserCaches(user);
                return user;
            });
    }

    public SignUpVM signUp(SignUpVM signUpVM) throws UnsupportedEncodingException {

        String command1="scl enable rh-python36 'python $MED/medblocks.py createuser" +
            " -n " + signUpVM.getName() +
            " -p " + signUpVM.getPhone() +
            " -e " + signUpVM.getEmail() +
            " -ut " + signUpVM.getType() +
            " -o $MED/" + signUpVM.getName() + ".json'";
        String returnKey = executeCommand(command1);
        System.out.println("from python: " + returnKey);
        byte[] encodedBytes = Base64.getEncoder().encode(returnKey.getBytes());
        signUpVM.setKey(new String(encodedBytes, "UTF-8"));
        //byte[] decodedBytes = Base64.getDecoder().decode(signUpVM.getKey());
        //System.out.println("decodedBytes " + new String(decodedBytes));
        return signUpVM;
    }


    public LoginResponseVM login(String key) {

        String command1="scl enable rh-python36 'python $MED/medblocks.py info -c " +
            key + "'";
        String returnKey = executeCommand(command1);
        LoginResponseVM vm = new LoginResponseVM();
        if (returnKey.contains("1")) {
            byte[] decodedBytes = Base64.getDecoder().decode(key);
            String dkey = new String(decodedBytes);
            String[] dkeyArr = dkey.split(",");
            for (String itr : dkeyArr) {
                System.out.println(itr);
            }

            if (dkeyArr[3].contains("doctor")) {
                vm.setType("doctor");
            } else if (dkeyArr[3].contains("patient")) {
                vm.setType("patient");
            } else if (dkeyArr[3].contains("lab")) {
                vm.setType("lab");
            }

            String[] phones = dkeyArr[1].split("\"");
            vm.setPhoneNumber(phones[phones.length-1]);
            vm.setStatus(true);

            //System.out.println((new String(decodedBytes)));
            return vm;
        } else {
            vm.setStatus(false);
            return vm;
        }

    }

    public List<GetFilesVM> getAllFiles(String key, String phone) throws InterruptedException {


        if (StringUtils.isEmpty(phone)) {
            byte[] decodedBytes = Base64.getDecoder().decode(key);
            String dkey = new String(decodedBytes);
            String[] dkeyArr = dkey.split(",");
            String[] phones = dkeyArr[1].split("\"");
            phone = phones[phones.length-1];
        }
        /*byte[] decodedBytes = Base64.getDecoder().decode(key);
        String dkey = new String(decodedBytes);
        String[] dkeyArr = dkey.split(",");
        String[] phones = dkeyArr[1].split("\"");*/
        String command = "scl enable rh-python36 'python $MED/medblocks.py list" +
            " -p " + phone +
            " -c " + key + "'";
        Thread.sleep(10000);
        String output = executeCommand(command);
        String[] outLines = output.split("\n");

        List<GetFilesVM> list = new ArrayList<>();
        for (int i =0 ; i < outLines.length ; i++) {

            if(outLines[i].trim().contains("ID")) {
                GetFilesVM vm = new GetFilesVM();
                vm.setFileId(outLines[i].trim().replace("ID: ", ""));
                vm.setFileIpfsHash(outLines[i + 1].trim().replace("IPFS hash: ", ""));
                vm.setFileName(outLines[i].trim().replace("ID: ", ""));
                vm.setPermitted(!outLines[i + 4].trim().contains("cannot"));
                list.add(vm);
                i = i + 4;
            }
        }
        List<FileAssets> filesFromDB = fileAssetsRepository.findAll();
        if (!CollectionUtils.isEmpty(list)) {
            for (GetFilesVM itr: list) {

                for (FileAssets fileAssetsDB : filesFromDB) {
                    if (fileAssetsDB.getFileId().equals(itr.getFileId())) {
                        itr.setFileName(fileAssetsDB.getFileName());
                        break;
                    }
                }
            }
        }
        System.out.println(list);
        return list;
    }

    public List<GetFilesVM> downLoadFile(String key, String fileId) throws InterruptedException, IOException, JSchException {


        String command = "scl enable rh-python36 'python $MED/medblocks.py get" +
            " -a " + fileId +
            " -c " + key + "'";
        Thread.sleep(10000);
        String output = executeCommand(command);
        String[] outLines = output.split("\n");

        copyRemoteToLocal("/home/matellio",
            "/Users/rahul/development/project/blockchain_proj/medblock_java/src/main/webapp/content/downloaded", fileId + ".json");
        return null;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public AddFileDTO addFile(MultipartFile file, AddFileDTO addFileDTO) throws IOException, JSchException, InterruptedException {

        storeFile(file);

        copyLocalToRemote(this.fileStorageLocation.toString(),
            "/home/matellio/rahul/medblocks", file.getOriginalFilename());

        String command = "scl enable rh-python36 'python $MED/medblocks.py add /home/matellio/rahul/medblocks/proxy.conf.json" +
            " -p " + addFileDTO.getPhoneNumber() +
            " -c " + addFileDTO.getKey() + "'";
        Thread.sleep(10000);
        String output = executeCommand(command).trim();
        String[] out = output.split("\n");

        for (String itr : out) {
            System.out.println(itr);
        }

        FileAssets fileAssets = new FileAssets();
        fileAssets.setFileName(file.getOriginalFilename());

        for (String itr : out) {
            if (itr.trim().contains("ipfs_hash: ")) {
                addFileDTO.setFileHashKey(itr.trim().replace("ipfs_hash: ", ""));
                fileAssets.setFileHashKey(itr.trim().replace("ipfs_hash: ", ""));
            }

            if (itr.trim().contains("[+] Created: ")) {
                addFileDTO.setFileId(itr.trim().replace("[+] Created: ", ""));
                fileAssets.setFileId(itr.trim().replace("[+] Created: ", ""));
            }
        }

        fileAssetsRepository.save(fileAssets);

        return addFileDTO;
    }

    public void permit(PermitVM permitVM) throws InterruptedException {

        String command = "scl enable rh-python36 'python $MED/medblocks.py permit" +
            " -as " + permitVM.getFileId() +
            " -c " + permitVM.getKey() +
            " -p " + permitVM.getPhoneNumber() + "'";
        Thread.sleep(10000);
        String output = executeCommand(command).trim();
        /*String[] out = output.split("\n");

        for (String itr : out) {
            System.out.println(itr);
        }

        for (String itr : out) {
            if (itr.trim().contains("ipfs_hash: ")) {
                addFileDTO.setFileHashKey(itr.trim().replace("ipfs_hash: ", ""));
            }

            if (itr.trim().contains("[+] Created: ")) {
                addFileDTO.setFileId(itr.trim().replace("[+] Created: ", ""));
            }
        }*/

        //return null;
    }

    private String executeCommand(String command1) {
        StringBuffer returnKey = new StringBuffer();
        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                    returnKey.append(new String(tmp, 0, i)).append("\n");
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnKey.toString();
    }

    private void copyLocalToRemote(String from, String to, String fileName) throws JSchException, IOException {
        boolean ptimestamp = true;
        from = from + File.separator + fileName;
        //from = fileName;

        // exec 'scp -t rfile' remotely
        String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + to;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        File _lfile = new File(from);

        if (ptimestamp) {
            command = "T" + (_lfile.lastModified() / 1000) + " 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }
        }

        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = _lfile.length();
        command = "C0644 " + filesize + " ";
        if (from.lastIndexOf('/') > 0) {
            command += from.substring(from.lastIndexOf('/') + 1);
        } else {
            command += from;
        }

        command += "\n";
        out.write(command.getBytes());
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        // send a content of lfile
        FileInputStream fis = new FileInputStream(from);
        byte[] buf = new byte[1024];
        while (true) {
            int len = fis.read(buf, 0, buf.length);
            if (len <= 0) break;
            out.write(buf, 0, len); //out.flush();
        }

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }
        out.close();

        try {
            if (fis != null) fis.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }

        channel.disconnect();
    }

    public int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //         -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    private void copyRemoteToLocal(String from, String to, String fileName) throws JSchException, IOException {
        from = from + File.separator + fileName;
        String prefix = null;

        if (new File(to).isDirectory()) {
            prefix = to + File.separator;
        }

        // exec 'scp -f rfile' remotely
        String command = "scp -f " + from;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] buf = new byte[1024];

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        while (true) {
            int c = checkAck(in);
            if (c != 'C') {
                break;
            }

            // read '0644 '
            in.read(buf, 0, 5);

            long filesize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ') break;
                filesize = filesize * 10L + (long) (buf[0] - '0');
            }

            String file = null;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }

            System.out.println("file-size=" + filesize + ", file=" + file);

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            // read a content of lfile
            FileOutputStream fos = new FileOutputStream(prefix == null ? to : prefix + file);
            int foo;
            while (true) {
                if (buf.length < filesize) foo = buf.length;
                else foo = (int) filesize;
                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    // error
                    break;
                }
                fos.write(buf, 0, foo);
                filesize -= foo;
                if (filesize == 0L) break;
            }

            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            try {
                if (fos != null) fos.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        channel.disconnect();
        //session.disconnect();
    }

    public User registerUser(UserDTO userDTO, String password) {

        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new LoginAlreadyUsedException();
            }
        });
        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail().toLowerCase());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    private boolean removeNonActivatedUser(User existingUser){
        if(existingUser.isActivated()) {
             return false;
        }
        userRepository.delete(existingUser);
        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email id of user
     * @param langKey language key
     * @param imageUrl image URL of user
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email.toLowerCase());
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update
     * @return updated user
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setEmail(userDTO.getEmail().toLowerCase());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            this.clearUserCaches(user);
            log.debug("Deleted User: {}", user);
        });
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public Optional<User> getUserWithAuthorities(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
    }
}
