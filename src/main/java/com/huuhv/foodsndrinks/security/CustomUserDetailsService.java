package com.huuhv.foodsndrinks.security;

import com.huuhv.foodsndrinks.repository.UserRepository;
import com.huuhv.foodsndrinks.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(loginId, loginId)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + loginId));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new DisabledException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin.");
        }

        String encodedPassword = user.getPassword();
        if (encodedPassword == null || encodedPassword.isBlank()) {
            // OAuth accounts may not have a local password
            throw new UsernameNotFoundException("Thông tin đăng nhập không chính xác.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                encodedPassword,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
