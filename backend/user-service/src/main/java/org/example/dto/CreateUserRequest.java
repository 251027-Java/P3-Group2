package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String passwordHash;

    private Double latitude;
    private Double longitude;
    private String role; // Optional, defaults to USER
}
