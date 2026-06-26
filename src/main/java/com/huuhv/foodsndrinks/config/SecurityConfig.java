package com.huuhv.foodsndrinks.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // enable @PreAuthorize / @Secured on individual methods
@RequiredArgsConstructor
public class SecurityConfig {

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
                .csrf(csrf -> csrf.disable())
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
                .csrf(csrf -> csrf.disable())   // stateless → CSRF not needed
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

    // -------------------------------------------------------
    // Chain 3: Web / Thymeleaf MVC — session + form login
    // -------------------------------------------------------
    @Bean
    @Order(3)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Static assets
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        // Public pages
                        .requestMatchers("/", "/menu", "/login", "/register", "/error").permitAll()
                        // Admin dashboard — ROLE_ADMIN only
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Anything else requires the user to be logged in
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // Redirect to a friendly 403 page instead of the default whitelabel
                        .accessDeniedPage("/error/403")
                );

        return http.build();
    }
}
