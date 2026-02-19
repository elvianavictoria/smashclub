package com.backendsyndicate.smashclub.common.security;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("User tidak ditemukan: " + userId);
                });

        // Map status ke Spring Security's account status
        boolean isAccountLocked = user.getStatus() == AuthenticationConstant.LOCKED;
        boolean isAccountDisabled = user.getStatus() == AuthenticationConstant.PENDING;
        boolean isAccountExpired = false; // Bisa diatur sesuai kebutuhan
        boolean isCredentialsExpired = false; // Bisa diatur sesuai kebutuhan

        log.debug("User loaded: {}, status: {}", user.getEmail(), user.getStatus());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getId()) // Gunakan ID sebagai username
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountLocked(isAccountLocked)
                .disabled(isAccountDisabled)
                .accountExpired(isAccountExpired)
                .credentialsExpired(isCredentialsExpired)
                .build();
    }

    /**
     * Helper method untuk mendapatkan User entity dari SecurityContext
     * Bisa dipanggil di controller dengan:
     * User user = ((CustomUserDetailsService)userDetailsService).getCurrentUser();
     */
    public User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}