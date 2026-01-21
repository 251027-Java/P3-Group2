// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade.client;

import com.marketplace.trade.client.dto.UserResponse;
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
}
