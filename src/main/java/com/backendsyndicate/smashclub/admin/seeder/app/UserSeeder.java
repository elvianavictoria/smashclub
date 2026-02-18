package com.backendsyndicate.smashclub.admin.seeder.app;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.booking.model.Coach;
import com.backendsyndicate.smashclub.booking.repo.CoachRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.ecommerce.service.CartService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional
@Profile("dev")
public class UserSeeder implements DataSeeder {
    private UserRepository userRepo;
    private WalletService walletService;
    private CartService cartService;
    private PasswordHasher passwordHasher;

    public UserSeeder(UserRepository userRepo, WalletService walletService, PasswordHasher passwordHasher, CartService cartService) {
        this.userRepo = userRepo;
        this.passwordHasher = passwordHasher;
        this.walletService = walletService;
        this.cartService = cartService;
    }

    @Override
    public void seed() {
        initUser();
    }

    public void initUser() {
        generateUser("Michael", "micdece69@gmail.com", "123456", CommonConstant.STATUS_ACTIVE);
    }

    private void generateUser(String fullName, String email, String password, int status) {
        User user = userRepo.findByEmail(email).orElseGet( () -> {
            User x = new User();
            x.setFullName(fullName);
            x.setEmail(email);
            x.setPasswordHash(passwordHasher.hash(password));
            x.setStatus((byte) status);
            x.setCreatedDate(LocalDateTime.now());

            return userRepo.save(x);
        } );

        walletService.createWallet(user.getId());
        cartService.getOrCreateActiveCart(user.getId());
    }
}
