package com.backendsyndicate.smashclub.admin.seeder.internal;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminUserRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class AdminUserSeeder implements DataSeeder {
    private final AdminUserRepo adminUserRepo;
    private final AdminRoleSeeder adminRoleSeeder;
    private final PasswordHasher passwordHasher;

    private static final long DEFAULT_USER_ID = 1L;

    public AdminUserSeeder(AdminUserRepo adminUserRepo, AdminRoleSeeder adminRoleSeeder) {
        this.adminUserRepo = adminUserRepo;
        this.adminRoleSeeder = adminRoleSeeder;
        this.passwordHasher = new PasswordHasher();
    }

    public void seed() {
        Logging.printConsole("Seeding user data...");
        initUser();
    }

    private void initUser() {
        Optional<AdminUser> opt = adminUserRepo.findById(DEFAULT_USER_ID);

        if( opt.isEmpty() ) {
            AdminUser user = new AdminUser();
            user.setUsername("developer");
            user.setFullName("Developer");
            user.setPassword(passwordHasher.hash("developer"));
            user.setAdminRole(adminRoleSeeder.getRoles().get(AdminConstant.ROLE_DEVELOPER));
            user.setStatus(CommonConstant.STATUS_ACTIVE);

            adminUserRepo.save(user);
        }
    }
}
