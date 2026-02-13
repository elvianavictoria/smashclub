package com.backendsyndicate.smashclub.common.security;

import com.backendsyndicate.smashclub.admin.security.jwt.AdminJwtFilter;
import com.backendsyndicate.smashclub.admin.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(authService);
//        return authProvider;
//    }

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
//                                       "/api/v1/admin/sales/**",
//                                       "/api/v1/admin/refund-request/**"
                               ).permitAll()
                               .anyRequest().authenticated()
                ).
//            headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())). // Allow H2 console to run in a frame
//                httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint)).
                exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)).
                sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                authenticationProvider(cmsAuthenticationProvider()).
                addFilterBefore(adminJwtFilter, UsernamePasswordAuthenticationFilter.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
    /*public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(
                        request->request.requestMatchers(
                                "/auth/**",
                                "/api/v1/auth/**",
                                "/transaction/**",
                                    "/booking/**",
                                    "/e-commerce/**",
                                    "/admin/**"
                        ).permitAll().anyRequest().authenticated());*/
//            headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())). // Allow H2 console to run in a frame
//        httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint)).
//                exceptionHandling(Customizer.withDefaults()).
//                sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
//                authenticationProvider(authenticationProvider()).
//                addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        //return http.build();
    //}
}
