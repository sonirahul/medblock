package com.medblock.service;

import java.io.*;
import java.util.Base64;

import com.jcraft.jsch.*;


public class JSchExampleSSHConnection {

    /**
     * JSch Example Tutorial
     * Java SSH Connection Program
     */
    public static void main(String[] args) {
        String host="192.168.10.179";
        String user="matellio";
        String password="matellio";
        //String command1="scl enable rh-python36 'python $MED/medblocks.py info -c eyJuYW1lIjogImJoYXJhdCIsICJwaG9uZSI6ICIxMjM0NTU0MzIxIiwgImVtYWlsIjogImJoYXJhdEBlbWFpbCIsICJiaWdjaGFpbiI6IHsicHJpdmF0ZV9rZXkiOiAiOUxDVXFaUDdaSFp1c3M4eUF3aVl3Y2I2aEdvSDFVekJGQTdNeGtVSzdRSHMiLCAicHVibGljX2tleSI6ICJBY3d1OTVyaEVVQmZoblhoSm82alZ5RHJRczNRdnZRTWQyTTlUWEdtbkU0MiJ9LCAicnNhIjogeyJwcml2YXRlX2tleSI6ICItLS0tLUJFR0lOIFJTQSBQUklWQVRFIEtFWS0tLS0tXG5NSUlDWHdJQkFBS0JnUURzbWdKWEdPNXo0bEcvSzUwWDB5Z2hNTTRVdzBJOExXbFRieHhmajRyTWFRcG5GY1NEXG5YQVBmOEdIaERLQndBSkVQeFp5SWZUR3hMeWk3MkI3c2FmNDZPM0JTbGp3WmVMM2x4dDFmQ0M0U3FmZ0tKY2ozXG44bzJlZDdPUUVXc1lyTVhWRCtTOVlMeXZyakVQR0J2U3UwUnVrNDBNVFNCcmxZdld2bGpkOThyNmh3SURBUUFCXG5Bb0dCQU1JK1BpV2JSa2pGU1MrVmNzY1lpQjJYbjlqVDVSRGFoMW9FSWxzaDBXVzM1dDV5dEg3VGtDck5nczNzXG5POXMzMitBQzhGdnR6Rm9XeVc1T0pEVWJYNmFDNEZWQzcyeWRORE82ZkwxZytqYkdaekQ3ZVI1S3RORFZhcEhBXG5SMDVWZTBmYnhhcDdoSUpyM2VBTWdOK05FVEJBZjlFRG9Kakx0WWhvUU9vSStwV2hBa0VBNzg2VVVHZi9zdFB1XG4rcGtUM2tQU0VxMitrNHE3aXR3amdtaGdnVkR0MExYZlNlNXdZU2JtYWdGSlpSMURuTVFZZXpQSEtkcEdQZElVXG40bVN0TElPWHZRSkJBUHlVQlQ4cC9wTEZYR3FOMVlTODVtN3A5VHF2aTFuTjIva1FtKy9VRDFyRkhxTzhPU2xPXG40Zkw1dUw3L3k3NzUyWURhYzg2WHdOVS8rSDNmU0ZQalRaTUNRUURKaG41SFVBNm5Gc21YV1R0RUp4U01VK3g3XG5DelJ1RG0vYzhLQmhMRVNlaDZqYzlYOEZkZTVlbVRRM3dDOEl1QmFtdXEyZHMyd2lHY0VwUEZmM3Q2SzFBa0VBXG5xTG9wRnh6eWx4R0l2eFdvd1ZkL2lEQjZSWTNuUlVLajVDb1lRWW9rQjdzTzBNcDRWUVFCSk5OMWV3SmVGV0hLXG56cTBnRjY2QWZ2U1JCMlVWYUJTOFNRSkJBTUJDdWt6UGY5YmFaZ2tMTWVjOVpYUkV1eU9DeVF6ZmUzYlRXS3B2XG5hYTlpVkJhVllNTk45aVR4dVZMbFl4eDNsdkJHbXZyUWEzVmZreWFZTWNBakpOVT1cbi0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0tIiwgInB1YmxpY19rZXkiOiAiLS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS1cbk1JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0R05BRENCaVFLQmdRRHNtZ0pYR081ejRsRy9LNTBYMHlnaE1NNFVcbncwSThMV2xUYnh4Zmo0ck1hUXBuRmNTRFhBUGY4R0hoREtCd0FKRVB4WnlJZlRHeEx5aTcyQjdzYWY0Nk8zQlNcbmxqd1plTDNseHQxZkNDNFNxZmdLSmNqMzhvMmVkN09RRVdzWXJNWFZEK1M5WUx5dnJqRVBHQnZTdTBSdWs0ME1cblRTQnJsWXZXdmxqZDk4cjZod0lEQVFBQlxuLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tIn19Cg'";

        //String command1="scl enable rh-python36 'python $MED/medblocks.py createuser" +
        //    " -n bharat -p 7654567853 -e bharat@gamil.com -o $MED/bharat.json'";

        String command1 = "ls -lrt /home/matellio";
        try{








            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");




            JSchExampleSSHConnection connection = new JSchExampleSSHConnection();
            //connection.copyLocalToRemote(session,
            //    "/Users/rahul/development/project/blockchain_proj/medblock_java",
            //    "/home/matellio", "angular.json");
            /*Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get("UserResource.java", "/home/matellio");
            sftpChannel.exit();
            session.disconnect();*/






            StringBuffer returnKey = new StringBuffer();
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
                    // System.out.print(new String(tmp, 0, i));
                    returnKey.append(new String(tmp, 0, i)).append("\n");
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            byte[] encodedBytes = Base64.getEncoder().encode(returnKey.toString().getBytes("UTF-8"));
            System.out.println("0:" + returnKey.toString());
            System.out.println("1:" + encodedBytes);
            System.out.println("2:" + new String(encodedBytes, "UTF-8"));
            //System.out.println("2:" + Base64.encodeToString(data, false););
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    /*private void copyLocalToRemote(Session session, String from, String to, String fileName) throws JSchException, IOException {
        boolean ptimestamp = true;
        from = from + File.separator + fileName;

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
        session.disconnect();
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
*/

}
