package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminUserRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
public class UserSeeder {
    @Autowired
    private final AdminUserRepo adminUserRepo;
    private final RoleSeeder roleSeeder;

    private static final long DEFAULT_USER_ID = 1L;

    public UserSeeder(AdminUserRepo adminUserRepo, RoleSeeder roleSeeder) {
        this.adminUserRepo = adminUserRepo;
        this.roleSeeder = roleSeeder;
    }

    protected void init() {
        initUser();
    }

    private void initUser() {
        Optional<AdminUser> opt = adminUserRepo.findById(DEFAULT_USER_ID);

        if( opt.isEmpty() ) {
            AdminUser user = new AdminUser();
            user.setUsername("developer");
            user.setFullName("Developer");
            user.setAdminRole(roleSeeder.getRoles().get(RoleSeeder.ROLE_DEVELOPER));
            user.setStatus(CommonConstant.STATUS_ACTIVE);

            adminUserRepo.save(user);
        }
    }
}
