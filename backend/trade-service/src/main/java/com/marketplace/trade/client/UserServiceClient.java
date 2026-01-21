// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.client;

import java.time.LocalDateTime;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feign client for communicating with user-service
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserResponse {
        private Long userId;
        private String email;
        private String username;
        private Double latitude;
        private Double longitude;
        private String role;
        private LocalDateTime createdAt;
    }
}
