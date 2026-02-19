package com.backendsyndicate.smashclub.common.security;

import com.backendsyndicate.smashclub.auth.model.Sessions;
import com.backendsyndicate.smashclub.auth.repository.SessionRepository;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // ============ 1. CEK APAKAH ADA HEADER AUTHORIZATION ============
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ============ 2. EKSTRAK TOKEN DARI HEADER ============
        final String jwt = authHeader.substring(7);
        final String userId;

        try {
            // ============ 3. VALIDASI FORMAT JWT ============
            if (!jwtService.isTokenValid(jwt)) {
                log.warn("Invalid JWT token format");
                filterChain.doFilter(request, response);
                return;
            }

            // ============ 4. CEK TOKEN DI DATABASE ============
            // Cek apakah token masih valid di tabel Sessions
            Sessions session = sessionRepository
                    .findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                            jwt,
                            AuthenticationConstant.TOKEN_TYPE_ACCESS,
                            LocalDateTime.now()
                    ).orElse(null);

            if (session == null) {
                log.warn("Token not found in database or has been invalidated");
                filterChain.doFilter(request, response);
                return;
            }

            // ============ 5. EKSTRAK USER ID ============
            userId = jwtService.extractUserId(jwt);

            // ============ 6. SET AUTHENTICATION KE SECURITY CONTEXT ============
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Update last accessed time
                session.setLastAccessedAt(LocalDateTime.now());
                sessionRepository.save(session);

                log.debug("Authenticated user: {}", userId);
            }

        } catch (Exception e) {
            log.error("JWT Filter error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Jangan filter untuk endpoint public (opsional, bisa diatur di sini)
        return path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/verify-email") ||
                path.startsWith("/api/v1/auth/forgot-password") ||
                path.startsWith("/api/v1/auth/reset-password");
    }
}