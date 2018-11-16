package com.medblock.service;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class JSchExampleSSHConnection {

    /**
     * JSch Example Tutorial
     * Java SSH Connection Program
     */
    public static void main(String[] args) {
        String host="192.168.10.179";
        String user="matellio";
        String password="matellio";
        String command1="scl enable rh-python36 'python $MED/medblocks.py info -c eyJuYW1lIjogIm1hbmlzaCIsICJwaG9uZSI6ICIxMjM0NTY3ODkiLCAiZW1haWwiOiAiaGVsbG9Ac2RmcyIsICJiaWdjaGFpbiI6IHsicHJpdmF0ZV9rZXkiOiAiODExeGhLWkVRNnJWdkJyM1F1VG53RWdINktvdlNWdlZTVlBzTFhtcHZEbXoiLCAicHVibGljX2tleSI6ICI4cGZ3NVFhajFMZDhWc20yNEJNeloyU3lvWTdSUkxvUjh2YnVYNVptdDkxcyJ9LCAicnNhIjogeyJwcml2YXRlX2tleSI6ICItLS0tLUJFR0lOIFJTQSBQUklWQVRFIEtFWS0tLS0tXG5NSUlDWEFJQkFBS0JnUURndjlKU1ZRcmtFTEdabHlXeFR6NTYrRldkVW1tZ2pPRjhDMmhTY1pBNDZsQnBGaXp3XG56MXJWVlU1bE8wc2tJaWlKSDE2aCtxaXQ3dmttejJGU3VPSkpiOXNFaDUxaXJBbDZucXRLNWFwc0YwZXRMZ3o5XG5NcWkwUVR1aUVzeG83MmJqaUZwTVg4dUhvc24waFhwR3JEVmNnc2hGT0lzYVM0VC9NcmExMWJBQ2p3SURBUUFCXG5Bb0dBUW1jS1dmNzhWOFBDNVZFdDlzUWwvcWtPaW92RjM0U2dQa2tVaW44NUVFZlNlQ253SHpuMGFXRnA1eWpzXG5tNEZvSHBOaEgxUnlyK2tTUGZBNW5mbzRDTks2Y0xDNHRCVG1hY1NQcHJZVytxWUxRQjdSa2U1LytudjJobHA3XG53bWI5clNqUFRWNGsyWE1Jd2p0YnM2ZWRLMXB6TThlN3drNklxcHZrYjh4NU1ha0NRUURyU240RngxL2RNQjNiXG5LeGhXRHdZYXcyT3BvaTdjTkZ0UlBhRy9DRmxPV2xYTnpuNmhkZlNiZnZpMGQzTXcwT0Y2QnNUc3dYUzB5N2xzXG40N3VRZlJLdEFrRUE5SWZPbzZuZVMrMWlLK09UckYzelFPSmtpazRBSkFFRklUYlZwa0F5QlR6SVBTTlFweURlXG5BY0FoMmh1bnQweXlRQ0cxUDFPSGNOaDhWQ3pPU1BETnF3SkJBS004VS8vNFdRYWdLaVp5V0hqa0JXMHQzd2ZCXG56OWJQc0FiRnhtQTlENUF2VmRYcGk2ckNwY2YzSjk0ei9NT0NOdHVzdEpRNGhwb2p1R25WK0x0K09pVUNRRnBNXG5ZRUZOdERvamtmSVZHdTQyejJJeGQrRWV4cXlFOStqNC85Smh1RmI0eUJUVG1xL3MwaTZoVFo3bVFYdk54YkVyXG5BV3crSXpESHNMbkF4ZmhuZS9zQ1FCRnEvUmQzS29hbmQzWkFYSzZYb0lZL1h6bkZlVFgxK3phVjVGendYZzJuXG52N3lhNVBYdnJhRkdKS1RwbWtTUXhlSFh6MUZqVXZxWEI2V0RHZTd2YnE0PVxuLS0tLS1FTkQgUlNBIFBSSVZBVEUgS0VZLS0tLS0iLCAicHVibGljX2tleSI6ICItLS0tLUJFR0lOIFBVQkxJQyBLRVktLS0tLVxuTUlHZk1BMEdDU3FHU0liM0RRRUJBUVVBQTRHTkFEQ0JpUUtCZ1FEZ3Y5SlNWUXJrRUxHWmx5V3hUejU2K0ZXZFxuVW1tZ2pPRjhDMmhTY1pBNDZsQnBGaXp3ejFyVlZVNWxPMHNrSWlpSkgxNmgrcWl0N3ZrbXoyRlN1T0pKYjlzRVxuaDUxaXJBbDZucXRLNWFwc0YwZXRMZ3o5TXFpMFFUdWlFc3hvNzJiamlGcE1YOHVIb3NuMGhYcEdyRFZjZ3NoRlxuT0lzYVM0VC9NcmExMWJBQ2p3SURBUUFCXG4tLS0tLUVORCBQVUJMSUMgS0VZLS0tLS0ifX0K'";
        try{

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
