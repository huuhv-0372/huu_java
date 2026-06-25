package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.repository.UserRepository;
import com.huuhv.foodsndrinks.dto.request.RegisterReqDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.AuthProvider;
import com.huuhv.foodsndrinks.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // Register new user
    @Transactional
    public void registerNewUser(RegisterReqDto registerReqDto) {
        log.info("Registering new user with username: {}", registerReqDto.getUsername());

        // Create new user and save to database
        var user = new User();
        user.setFullName(registerReqDto.getFullName());
        user.setUsername(registerReqDto.getUsername());
        user.setEmail(registerReqDto.getEmail());
        user.setPhone(registerReqDto.getPhone());
        user.setPassword(passwordEncoder.encode(registerReqDto.getPassword())); // Password should be encoded before saving
        user.setRole(Role.ROLE_USER); // Set default role to ROLE_USER
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setIsActive(true); // Set default active status to true

        userRepository.save(user);

        log.info("User registered successfully with username: {}", registerReqDto.getUsername());
    }
}
