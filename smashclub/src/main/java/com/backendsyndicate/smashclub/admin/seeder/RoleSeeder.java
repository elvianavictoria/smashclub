package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.repo.AdminRoleRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//@Component
public class RoleSeeder {
    @Autowired
    private final AdminRoleRepo adminRoleRepo;
    private final MenuSeeder menuSeeder;
    private final PermissionSeeder permissionSeeder;

    @Getter
    private final Map<Integer, AdminRole> roles = null;
    protected static final int ROLE_DEVELOPER = 1;
    protected static final int ROLE_ADMIN = 2;
    protected static final int ROLE_USER = 3;

    private static Set<AdminMenu> developerMenuSet;
    private static Set<AdminPermission> developerPermissionSet;

    private static Set<AdminMenu> adminMenuSet;
    private static Set<AdminPermission> adminPermissionSet;

    private static Set<AdminMenu> userMenuSet;
    private static Set<AdminPermission> userPermissionSet;

    public RoleSeeder(AdminRoleRepo adminRoleRepo, MenuSeeder menuSeeder, PermissionSeeder permissionSeeder) {
        this.adminRoleRepo = adminRoleRepo;
        this.menuSeeder = menuSeeder;
        this.permissionSeeder = permissionSeeder;

        this.developerMenuSet = Set.of();
        this.developerPermissionSet = Set.of();
        this.adminMenuSet = Set.of();
        this.adminPermissionSet = Set.of();
        this.userMenuSet = Set.of();
        this.userPermissionSet = Set.of();

        init();
    }

    protected void init() {
        initPermissionSet();
        initRole();
    }

    private void initPermissionSet() {
        developerMenuSet.addAll(menuSeeder.getMenus().values());
        developerPermissionSet.addAll(permissionSeeder.getPermissions().values());

        adminMenuSet.addAll(menuSeeder.getMenus().values());
        adminMenuSet.remove(menuSeeder.getMenus().get(MenuSeeder.MENU_ROLES));
        adminMenuSet.remove(menuSeeder.getMenus().get(MenuSeeder.MENU_USER));
        adminPermissionSet.addAll(permissionSeeder.getPermissions().values());
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_ROLES_READ));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_ROLES_CREATE));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_ROLES_EDIT));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_ROLES_DELETE));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_USERS_READ));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_USERS_CREATE));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_USERS_EDIT));
        adminPermissionSet.remove(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_USERS_DELETE));

        userMenuSet.addAll(menuSeeder.getMenus().values());
        userMenuSet.remove(menuSeeder.getMenus().get(MenuSeeder.MENU_ROLES));
        userMenuSet.remove(menuSeeder.getMenus().get(MenuSeeder.MENU_USER));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_COURT_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_COACH_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_EQUIPMENT_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_PRODUCT_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_SALES_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_SALES_DETAIL));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_BOOKING_SALES_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_BOOKING_SALES_DETAIL));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_PRODUCT_SALES_READ));
        userPermissionSet.add(permissionSeeder.getPermissions().get(PermissionSeeder.PERMISSION_PRODUCT_SALES_DETAIL));
    }

    private void initRole() {
        generateRoleItem(ROLE_DEVELOPER, "DEV", "Developer", developerMenuSet, developerPermissionSet, CommonConstant.STATUS_ACTIVE);
        generateRoleItem(ROLE_ADMIN, "ADM", "Administrator", adminMenuSet, adminPermissionSet, CommonConstant.STATUS_ACTIVE);
        generateRoleItem(ROLE_USER, "USR", "User", userMenuSet, userPermissionSet, CommonConstant.STATUS_ACTIVE);
    }

    private void generateRoleItem(int id, String roleCode, String roleName, Set<AdminMenu> menuSet, Set<AdminPermission> permissionSet, int status) {
        Optional<AdminRole> opt = adminRoleRepo.findById(id);
        if( opt.isEmpty() ) {
            AdminRole adminRole = new AdminRole();
            adminRole.setId(id);
            adminRole.setRoleCode(roleCode);
            adminRole.setRoleName(roleName);
            adminRole.setStatus(status);
            adminRole.setMenuSet(menuSet);
            adminRole.setPermissionSet(permissionSet);

            roles.put(id, adminRole);
            adminRoleRepo.save(adminRole);
        }
    }
}
