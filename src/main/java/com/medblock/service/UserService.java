package com.medblock.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.medblock.config.Constants;
import com.medblock.domain.Authority;
import com.medblock.domain.User;
import com.medblock.repository.AuthorityRepository;
import com.medblock.repository.UserRepository;
import com.medblock.security.AuthoritiesConstants;
import com.medblock.security.SecurityUtils;
import com.medblock.service.dto.UserDTO;
import com.medblock.service.util.RandomUtil;
import com.medblock.web.rest.errors.*;

import com.medblock.web.rest.vm.SignUpVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

    @Autowired
    private Session session;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
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
            " -o $MED/" + signUpVM.getName() + ".json'";
        String returnKey = executeCommand(command1);
        System.out.println("from python: " + returnKey);
        byte[] encodedBytes = Base64.getEncoder().encode(returnKey.getBytes());
        signUpVM.setKey(new String(encodedBytes, "UTF-8"));
        //byte[] decodedBytes = Base64.getDecoder().decode(signUpVM.getKey());
        //System.out.println("decodedBytes " + new String(decodedBytes));
        return signUpVM;
    }


    public boolean login(String key) throws UnsupportedEncodingException {

        String command1="scl enable rh-python36 'python $MED/medblocks.py info -c eyJuYW1lIjogIm1hbmlzaCIsICJwaG9uZSI6ICIxMjM0NTY3ODkiLCAiZW1haWwiOiAiaGVsbG9Ac2RmcyIsICJiaWdjaGFpbiI6IHsicHJpdmF0ZV9rZXkiOiAiODExeGhLWkVRNnJWdkJyM1F1VG53RWdINktvdlNWdlZTVlBzTFhtcHZEbXoiLCAicHVibGljX2tleSI6ICI4cGZ3NVFhajFMZDhWc20yNEJNeloyU3lvWTdSUkxvUjh2YnVYNVptdDkxcyJ9LCAicnNhIjogeyJwcml2YXRlX2tleSI6ICItLS0tLUJFR0lOIFJTQSBQUklWQVRFIEtFWS0tLS0tXG5NSUlDWEFJQkFBS0JnUURndjlKU1ZRcmtFTEdabHlXeFR6NTYrRldkVW1tZ2pPRjhDMmhTY1pBNDZsQnBGaXp3XG56MXJWVlU1bE8wc2tJaWlKSDE2aCtxaXQ3dmttejJGU3VPSkpiOXNFaDUxaXJBbDZucXRLNWFwc0YwZXRMZ3o5XG5NcWkwUVR1aUVzeG83MmJqaUZwTVg4dUhvc24waFhwR3JEVmNnc2hGT0lzYVM0VC9NcmExMWJBQ2p3SURBUUFCXG5Bb0dBUW1jS1dmNzhWOFBDNVZFdDlzUWwvcWtPaW92RjM0U2dQa2tVaW44NUVFZlNlQ253SHpuMGFXRnA1eWpzXG5tNEZvSHBOaEgxUnlyK2tTUGZBNW5mbzRDTks2Y0xDNHRCVG1hY1NQcHJZVytxWUxRQjdSa2U1LytudjJobHA3XG53bWI5clNqUFRWNGsyWE1Jd2p0YnM2ZWRLMXB6TThlN3drNklxcHZrYjh4NU1ha0NRUURyU240RngxL2RNQjNiXG5LeGhXRHdZYXcyT3BvaTdjTkZ0UlBhRy9DRmxPV2xYTnpuNmhkZlNiZnZpMGQzTXcwT0Y2QnNUc3dYUzB5N2xzXG40N3VRZlJLdEFrRUE5SWZPbzZuZVMrMWlLK09UckYzelFPSmtpazRBSkFFRklUYlZwa0F5QlR6SVBTTlFweURlXG5BY0FoMmh1bnQweXlRQ0cxUDFPSGNOaDhWQ3pPU1BETnF3SkJBS004VS8vNFdRYWdLaVp5V0hqa0JXMHQzd2ZCXG56OWJQc0FiRnhtQTlENUF2VmRYcGk2ckNwY2YzSjk0ei9NT0NOdHVzdEpRNGhwb2p1R25WK0x0K09pVUNRRnBNXG5ZRUZOdERvamtmSVZHdTQyejJJeGQrRWV4cXlFOStqNC85Smh1RmI0eUJUVG1xL3MwaTZoVFo3bVFYdk54YkVyXG5BV3crSXpESHNMbkF4ZmhuZS9zQ1FCRnEvUmQzS29hbmQzWkFYSzZYb0lZL1h6bkZlVFgxK3phVjVGendYZzJuXG52N3lhNVBYdnJhRkdKS1RwbWtTUXhlSFh6MUZqVXZxWEI2V0RHZTd2YnE0PVxuLS0tLS1FTkQgUlNBIFBSSVZBVEUgS0VZLS0tLS0iLCAicHVibGljX2tleSI6ICItLS0tLUJFR0lOIFBVQkxJQyBLRVktLS0tLVxuTUlHZk1BMEdDU3FHU0liM0RRRUJBUVVBQTRHTkFEQ0JpUUtCZ1FEZ3Y5SlNWUXJrRUxHWmx5V3hUejU2K0ZXZFxuVW1tZ2pPRjhDMmhTY1pBNDZsQnBGaXp3ejFyVlZVNWxPMHNrSWlpSkgxNmgrcWl0N3ZrbXoyRlN1T0pKYjlzRVxuaDUxaXJBbDZucXRLNWFwc0YwZXRMZ3o5TXFpMFFUdWlFc3hvNzJiamlGcE1YOHVIb3NuMGhYcEdyRFZjZ3NoRlxuT0lzYVM0VC9NcmExMWJBQ2p3SURBUUFCXG4tLS0tLUVORCBQVUJMSUMgS0VZLS0tLS0ifX0K'";
        String returnKey = executeCommand(command1);
        if (returnKey.equals("1")) {
            System.out.println("bale bale");
        } else {
            System.out.println("booh");
        }
        return true;
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
                    returnKey.append(new String(tmp, 0, i));
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
