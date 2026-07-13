package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.request.LoginReqDto;
import com.huuhv.foodsndrinks.dto.response.AuthResDto;
import com.huuhv.foodsndrinks.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Xác thực username/password và cấp JWT.
     * Sai thông tin đăng nhập → AuthenticationManager ném BadCredentialsException,
     * tài khoản bị khóa → DisabledException; cả hai được ApiExceptionHandler map về 401.
     */
    public AuthResDto login(LoginReqDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        log.info("User '{}' logged in via API", userDetails.getUsername());
        return AuthResDto.builder()
                .accessToken(token)
                .expiresIn(jwtUtil.getExpirationTime() / 1000)
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }
}
