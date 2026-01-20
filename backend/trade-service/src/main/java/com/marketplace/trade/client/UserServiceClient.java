// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with user-service
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);
    
    class UserResponse {
        private Long appUserId;
        private String email;
        private String username;
        
        public Long getAppUserId() { return appUserId; }
        public void setAppUserId(Long appUserId) { this.appUserId = appUserId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
