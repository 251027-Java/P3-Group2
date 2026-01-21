package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserResponse;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Optional<UserResponse> createUser(CreateUserRequest request) {
        // Validate unique constraints

        if (userRepository.existsByUsername(request.getUsername())) {
            return Optional.empty();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return Optional.empty();
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(hashPassword(request.getPassword()));
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        User savedUser = userRepository.save(user);

        return Optional.of(UserResponse.fromUser(savedUser));
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return UserResponse.fromUser(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        return UserResponse.fromUser(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return UserResponse.fromUser(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::fromUser).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Check if email is being changed and if it already exists
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Check if username is being changed and if it already exists
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getLatitude() != null) {
            user.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            user.setLongitude(request.getLongitude());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        userRepository.deleteById(userId);
    }

    /**
     * Internal method for auth-service to get user with password hash.
     */
    public org.example.dto.AuthUserResponse getUserForAuth(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return org.example.dto.AuthUserResponse.fromUser(user);
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
