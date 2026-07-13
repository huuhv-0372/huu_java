package com.huuhv.foodsndrinks.config;

import com.huuhv.foodsndrinks.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // enable @PreAuthorize / @Secured on individual methods
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.security.remember-me-key}")
    private String rememberMeKey;

    // -------------------------------------------------------
    // Chain 1: Actuator endpoints — ROLE_ADMIN only
    // Kept first (highest priority) so the web/api chains
    // never accidentally open an actuator path.
    // -------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Basic liveness/readiness probe — no credentials needed
                        .requestMatchers("/actuator/health").permitAll()
                        // Everything else (metrics, info, caches, env…) → admins only
                        .anyRequest().hasRole("ADMIN")
                );

        return http.build();
    }

    // -------------------------------------------------------
    // Chain 2: REST API — stateless, JWT-based (no session)
    // -------------------------------------------------------
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/api-docs/**")
                .csrf(AbstractHttpConfigurer::disable)   // stateless → CSRF not needed
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI & OpenAPI spec (tighten or remove in prod via springdoc.swagger-ui.enabled=false)
                        .requestMatchers("/api-docs/**", "/api/v1/api-docs/**").permitAll()
                        // Auth endpoints (login, register, refresh-token) — public
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Every other API call must be authenticated
                        .anyRequest().authenticated()
                );
        // TODO: plug in JwtAuthenticationFilter once JWT service is ready:
        // http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {

            // Honor any saved request (e.g. user was trying to access /profile before being redirected to /login)
            var requestCache = new HttpSessionRequestCache();
            var savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                new org.springframework.security.web.DefaultRedirectStrategy()
                        .sendRedirect(request, response, savedRequest.getRedirectUrl());
                return;
            }

            // Lấy danh sách các quyền (Roles) của User hiện tại
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            String redirectUrl = "/"; // Mặc định nếu không rơi vào các case dưới

            for (GrantedAuthority grantedAuthority : authorities) {
                String authorityName = grantedAuthority.getAuthority();

                if (authorityName.equals(Role.ROLE_ADMIN.name())) {
                    redirectUrl = "/admin"; // Admin thì vào trang quản trị
                    break;
                } else if (authorityName.equals(Role.ROLE_USER.name())) {
                    redirectUrl = "/"; // User thường thì về trang chủ cửa hàng
                    break;
                }
            }

            // Thực hiện redirect thực tế
            new DefaultRedirectStrategy()
                    .sendRedirect(request, response, request.getContextPath() + redirectUrl);
        };
    }

    // -------------------------------------------------------
    // Chain 3: Web / Thymeleaf MVC — session + form login
    // -------------------------------------------------------
    @Bean
    @Order(3)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Static assets
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico").permitAll()
                        // Rating submission requires login — must be matched before the public /products/** rule below
                        .requestMatchers("/products/*/rate").hasAnyRole("USER", "ADMIN")
                        // Public pages
                        .requestMatchers("/", "/menu", "/products", "/products/**", "/categories/**", "/contact", "/login", "/register", "/error").permitAll()
                        // Admin dashboard — ROLE_ADMIN only
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")

                        .requestMatchers("/profile", "/cart/**", "/orders/**", "/suggest").hasAnyRole("USER", "ADMIN")
                        // Anything else requires the user to be logged in
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")

                        // THAY THẾ .defaultSuccessUrl bằng .successHandler vừa tạo ở trên
                        .successHandler(customAuthenticationSuccessHandler())

                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .rememberMeParameter("remember-me")
                )
                .exceptionHandling(ex -> ex
                        // Redirect to a friendly 403 page instead of the default whitelabel
                        .accessDeniedPage("/error")
                );

        return http.build();
    }
}
