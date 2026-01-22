package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.User;

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

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setLatitude(user.getLatitude());
        response.setLongitude(user.getLongitude());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static UserResponse fromAuthUser(AuthUserResponse authUser) {
        UserResponse response = new UserResponse();
        response.setUserId(authUser.getUserId());
        response.setEmail(authUser.getEmail());
        response.setUsername(authUser.getUsername());
        response.setRole(authUser.getRole());
        return response;
    }
}
