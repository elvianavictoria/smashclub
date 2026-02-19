package com.backendsyndicate.smashclub.common.security;

import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtFilter;
import com.backendsyndicate.smashclub.admin.security.ratelimit.AdminRateLimitFilter;
import com.backendsyndicate.smashclub.admin.service.AdminAuthService;
import com.backendsyndicate.smashclub.auth.service.AuthUserDetailsService;
import com.backendsyndicate.smashclub.common.security.JwtService;
import com.backendsyndicate.smashclub.auth.service.AuthService;
import com.backendsyndicate.smashclub.common.config.OtherConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private AdminJwtFilter adminJwtFilter;
    @Autowired
    private AdminRateLimitFilter adminRateLimitFilter;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    private AuthenticationEntryPoint authenticationEntryPoint;

    //    @Autowired
//    @Autowired
//    private JwtFilter jwtFilter;
//
//    @Autowired
//    @Qualifier("customAuthenticationEntryPoint")
//    private AuthenticationEntryPoint authenticationEntryPoint;
//
//    @Autowired
//    private AuthService2 authService;
//
//    /*
//        401 -> Otentikasi
//        403 -> Forbiden / Otorisasi
//     */
//
    @Bean
    public AuthenticationProvider cmsAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(adminAuthService);
        return authProvider;
    }

    @Bean
    public AuthenticationProvider communityAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(authUserDetailsService);
        return authProvider;
    }

    /**
     * CMS security procedure here
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(1)
    public SecurityFilterChain cmsSecurityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable).
                cors(cors -> cors.configurationSource(corsConfigurationSource())).
                // Endpoints that need authorization
                        securityMatcher("/api/v1/admin/**").
                authorizeHttpRequests(
                        auth -> auth
                                // Endpoints that open for public
                                .requestMatchers(
                                        "/api/v1/admin/auth/login"
                                        // Test
//                                        "/api/v1/admin/sales/**",
//                                        "/api/v1/admin/refund-request/**"
                                )
                                .permitAll()
                                .anyRequest().authenticated()
                ).
//            headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())). // Allow H2 console to run in a frame
//                httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint)).
        exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)).
                sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                authenticationProvider(cmsAuthenticationProvider()).
                addFilterBefore(adminRateLimitFilter, UsernamePasswordAuthenticationFilter.class).
                addFilterBefore(adminJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * App security procedure here
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(2)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/v1/admin/**")
                .authorizeHttpRequests(auth -> auth
                        // Public admin endpoints
                        .requestMatchers(
                                "/api/v1/admin/auth/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(cmsAuthenticationProvider())
                .addFilterBefore(adminJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Community App Security Configuration
     * Lengkap dengan auth module Anda
     */
    @Bean
    @Order(2)
    public SecurityFilterChain communitySecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/v1/**")
                .authorizeHttpRequests(auth -> auth

                        // ============ PUBLIC ENDPOINTS (TANPA LOGIN) ============

                        // 1. AUTH MODULE - Registrasi, Login, dll
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/verify-otp",
                                "/api/v1/auth/verify-email",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/validate-reset-token",
                                "/api/v1/auth/resend-verification",
                                "/api/v1/auth/resend-otp",
                                "/api/v1/auth/refresh-token"
                        ).permitAll()

                        // 2. BOOKING MODULE - Lihat lapangan & ketersediaan
                        .requestMatchers(
                                "/api/v1/booking/courts",
                                "/api/v1/booking/availability/**",
                                "/api/v1/booking/summary",
                                "/api/v1/booking/{bookingCode}"
                        ).permitAll()

                        // 3. PUBLIC UTILITIES
                        .requestMatchers(
                                "/api/v1/public/**",
                                "/error"
                        ).permitAll()

                        // ============ PROTECTED ENDPOINTS (PERLU LOGIN) ============

                        // AUTH MODULE - Endpoint yang butuh login
                        .requestMatchers(
                                "/api/v1/auth/logout",
                                "/api/v1/auth/logout-all",
                                "/api/v1/auth/check-session"
                        ).authenticated()

                        // PROFILE MODULE - Semua butuh login
                        .requestMatchers(
                                "/api/v1/profile",
                                "/api/v1/profile/**"
                        ).authenticated()

                        // BOOKING MODULE - Transaksi butuh login
                        .requestMatchers(
                                "/api/v1/booking",
                                "/api/v1/booking/my-bookings",
                                "/api/v1/booking/{bookingCode}/payment",
                                "/api/v1/booking/{bookingCode}/start",
                                "/api/v1/booking/{bookingCode}/complete"
                        ).authenticated()

                        // Sisanya butuh login
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userDetailsService) // Tambahkan UserDetailsService
                .authenticationProvider(communityAuthenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Fallback untuk static resources
     */
    @Bean
    @Order(3)
    public SecurityFilterChain fallbackSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/static/**", "/css/**", "/js/**", "/images/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(OtherConfig.getCorsUrlAllowed().split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
