package com.medblock.config;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BigChainDBConfig {

    /*@Bean
    public boolean configureBigChainDB() {
        BigchainDbConfigBuilder
            .baseUrl("https://test.bigchaindb.com") //or use http://testnet.bigchaindb.com
            .addToken("app_id", "d5596f54")
            .addToken("app_key", "6f108c571fcb25c4d34056bede9d246f").setup();

        return true;
    }*/
}
