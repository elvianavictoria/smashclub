package com.backendsyndicate.smashclub.admin.provider;

import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.service.AdminAuthService;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private AdminAuthService adminAuthService;
    @Autowired
    private PasswordHasher passwordHasher;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails adminUser = adminAuthService.loadUserByUsername(username);

        if (adminUser.getUsername().equals(username) && passwordHasher.verify(password, adminUser.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    adminUser.getAuthorities()
            );
        }

        return null; // important: tells Spring to try next provider
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
