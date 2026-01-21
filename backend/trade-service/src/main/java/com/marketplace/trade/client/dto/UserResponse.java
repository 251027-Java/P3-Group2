package com.marketplace.trade.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
