package com.backendsyndicate.smashclub.admin.seeder.internal;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
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
        generateUser(DEFAULT_USER_ID, "developer", "Developer", "developer", adminRoleSeeder.getRoles().get(AdminConstant.ROLE_DEVELOPER), CommonConstant.STATUS_ACTIVE);
        generateUser(2L, "admin", "Administrator", "admin", adminRoleSeeder.getRoles().get(AdminConstant.ROLE_ADMIN), CommonConstant.STATUS_ACTIVE);
        generateUser(3L, "user01", "Michael", "user01", adminRoleSeeder.getRoles().get(AdminConstant.ROLE_USER), CommonConstant.STATUS_ACTIVE);
    }

    private void generateUser(Long id, String username, String fullName, String password, AdminRole adminRole, int status) {
        Optional<AdminUser> opt = adminUserRepo.findById(id);

        if( opt.isEmpty() ) {
            AdminUser user = new AdminUser();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setPassword(passwordHasher.hash(password));
            user.setAdminRole(adminRole);
            user.setStatus(status);

            adminUserRepo.save(user);
        }
    }
}
