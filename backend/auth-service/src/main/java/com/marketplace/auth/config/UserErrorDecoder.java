package com.marketplace.auth.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new RuntimeException("User not found");
        } else if (response.status() == HttpStatus.CONFLICT.value()) {
            return new RuntimeException("User already exists");
        }
        return new RuntimeException("User service error: " + response.body().toString());
    }
}
