package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.request.RegisterReqDto;
import com.huuhv.foodsndrinks.dto.request.UserEditReqDto;
import com.huuhv.foodsndrinks.dto.response.UserResDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.AuthProvider;
import com.huuhv.foodsndrinks.enums.Role;
import com.huuhv.foodsndrinks.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // -------------------------------------------------------
    // Existence checks (used by registration)
    // -------------------------------------------------------

    public boolean existsByUsername(String username) { return userRepository.existsByUsername(username); }
    public boolean existsByEmail(String email)       { return userRepository.existsByEmail(email); }
    public boolean existsByPhone(String phone)       { return userRepository.existsByPhone(phone); }

    // -------------------------------------------------------
    // Registration
    // -------------------------------------------------------

    @Transactional
    public void registerNewUser(RegisterReqDto dto) {
        log.info("Registering new user: {}", dto.getUsername());
        var user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User registered: {}", dto.getUsername());
    }

    // -------------------------------------------------------
    // Admin: Search
    // -------------------------------------------------------

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<UserResDto> searchUsers(String keyword, String roleStr,
                                        String isActiveStr, int page, int size) {
        Role role = parseRole(roleStr);
        Boolean isActive = parseBoolean(isActiveStr);
        String kw = blank(keyword) ? null : keyword.trim();

        return userRepository.search(kw, role, isActive, PageRequest.of(Math.max(page, 0), size))
                .map(UserResDto::from);
    }

    // -------------------------------------------------------
    // Admin: Get for edit
    // -------------------------------------------------------

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UserEditReqDto getUserForEdit(Long id) {
        User u = findById(id);
        UserEditReqDto dto = new UserEditReqDto();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole());
        dto.setIsActive(u.getIsActive());
        return dto;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UserResDto getUserDetail(Long id) {
        return UserResDto.from(findById(id));
    }

    // -------------------------------------------------------
    // Admin: Update
    // -------------------------------------------------------

    @Transactional
    public void updateUser(Long id, UserEditReqDto dto) {
        User user = findById(id);

        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác!");
        }
        if (userRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi tài khoản khác!");
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive() != null && dto.getIsActive());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(user);
        log.info("Admin updated user #{}: {}", id, user.getUsername());
    }

    // -------------------------------------------------------
    // Admin: Toggle active status (quick action from list)
    // -------------------------------------------------------

    @Transactional
    public void toggleActive(Long id) {
        User user = findById(id);
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user #" + id));
    }

    private static Role parseRole(String value) {
        if (blank(value)) return null;
        try { return Role.valueOf(value.trim().toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException e) { return null; }
    }

    private static Boolean parseBoolean(String value) {
        if (blank(value)) return null;
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "1"  -> Boolean.TRUE;
            case "false", "0" -> Boolean.FALSE;
            default -> null;
        };
    }

    private static boolean blank(String s) { return s == null || s.isBlank(); }
}
