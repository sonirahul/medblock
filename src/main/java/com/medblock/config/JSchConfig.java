package com.medblock.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSchConfig {

    @Bean
    public Session session() throws JSchException {

        String host = "103.7.129.174";
        String user = "matellio";
        String password = "cgtjaipur123";
        //String command1="scl enable rh-python36 'python $MED/medblocks.py createuser -n kapila -p 7894567891 -e kapila@gmail.com -o $MED/kapila.json'";

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        session.setConfig(config);
        session.connect();
        System.out.println("Connected");

        return session;
    }
}
