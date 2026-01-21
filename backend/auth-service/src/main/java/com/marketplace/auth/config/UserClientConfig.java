package com.marketplace.auth.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserErrorDecoder();
    }
}
