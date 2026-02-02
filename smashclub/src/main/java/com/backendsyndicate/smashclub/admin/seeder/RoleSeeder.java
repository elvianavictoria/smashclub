package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.repo.AdminRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RoleSeeder {
    @Autowired
    private static AdminRoleRepo adminRoleRepo;
    private static Set<AdminMenu> developerMenuSet;
    private static Set<AdminPermission> developerPermissionSet;

    private static Set<AdminMenu> adminMenuSet;
    private static Set<AdminPermission> adminPermissionSet;

    private static Set<AdminMenu> userMenuSet;
    private static Set<AdminPermission> userPermissionSet;

    public static void init() {
        initRole();
    }

    private static void initRole() {
        generateRoleItem("DEV", "Developer", developerMenuSet, developerPermissionSet, 1);
        generateRoleItem("ADM", "Administrator", adminMenuSet, adminPermissionSet, 1);
        generateRoleItem("USR", "User", userMenuSet, userPermissionSet, 1);
    }

    private static void generateRoleItem(String roleCode, String roleName, Set<AdminMenu> menuSet, Set<AdminPermission> permissionSet, int status) {
        AdminRole adminRole = new AdminRole();
        adminRole.setRoleCode(roleCode);
        adminRole.setRoleName(roleName);
        adminRole.setStatus(status);
        adminRole.setMenuSet(menuSet);
        adminRole.setPermissionSet(permissionSet);

        adminRoleRepo.save(adminRole);
    }
}
