package com.backendsyndicate.smashclub.admin.seeder.internal;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.repo.AdminRoleRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional
public class AdminRoleSeeder implements DataSeeder {
    private final AdminRoleRepo adminRoleRepo;
    private final AdminMenuSeeder adminMenuSeeder;
    private final AdminPermissionSeeder adminPermissionSeeder;

    @Getter
    private Map<Integer, AdminRole> roles = null;

    private final Set<AdminMenu> developerMenuSet;
    private final Set<AdminPermission> developerPermissionSet;

    private final Set<AdminMenu> adminMenuSet;
    private final Set<AdminPermission> adminPermissionSet;

    private final Set<AdminMenu> userMenuSet;
    private final Set<AdminPermission> userPermissionSet;

    public AdminRoleSeeder(AdminRoleRepo adminRoleRepo, AdminMenuSeeder adminMenuSeeder, AdminPermissionSeeder adminPermissionSeeder) {
        this.adminRoleRepo = adminRoleRepo;
        this.adminMenuSeeder = adminMenuSeeder;
        this.adminPermissionSeeder = adminPermissionSeeder;

        this.roles = new HashMap<Integer, AdminRole>();
        this.developerMenuSet = new HashSet<AdminMenu>();
        this.developerPermissionSet = new HashSet<AdminPermission>();
        this.adminMenuSet = new HashSet<AdminMenu>();
        this.adminPermissionSet = new HashSet<AdminPermission>();
        this.userMenuSet = new HashSet<AdminMenu>();
        this.userPermissionSet = new HashSet<AdminPermission>();
    }

    public void seed() {
        Logging.printConsole("Seeding role data...");
        initPermissionSet();
        initRole();
    }

    private void initPermissionSet() {
        developerMenuSet.addAll(adminMenuSeeder.getMenus().values());
        developerPermissionSet.addAll(adminPermissionSeeder.getPermissions().values());

        adminMenuSet.addAll(adminMenuSeeder.getMenus().values());
        adminPermissionSet.addAll(adminPermissionSeeder.getPermissions().values());
        adminPermissionSet.remove(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_ROLES_CREATE));
        adminPermissionSet.remove(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_ROLES_EDIT));
        adminPermissionSet.remove(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_ROLES_DELETE));
        adminPermissionSet.remove(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_USERS_EDIT));
        adminPermissionSet.remove(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_USERS_DELETE));

        userMenuSet.addAll(adminMenuSeeder.getMenus().values());
        userMenuSet.remove(adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        userMenuSet.remove(adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        userMenuSet.remove(adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        // Test
        userMenuSet.remove(adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));

        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_DASHBOARD_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_COURT_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_COACH_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_EQUIPMENT_READ));
//        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_PRODUCT_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_SALES_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_SALES_DETAIL));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_BOOKING_SALES_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_BOOKING_SALES_DETAIL));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_PRODUCT_SALES_READ));
        userPermissionSet.add(adminPermissionSeeder.getPermissions().get(AdminConstant.PERMISSION_PRODUCT_SALES_DETAIL));
    }

    private void initRole() {
        generateRoleItem(AdminConstant.ROLE_DEVELOPER, "DEV", "Developer", developerMenuSet, developerPermissionSet, CommonConstant.STATUS_ACTIVE);
        generateRoleItem(AdminConstant.ROLE_ADMIN, "ADM", "Administrator", adminMenuSet, adminPermissionSet, CommonConstant.STATUS_ACTIVE);
        generateRoleItem(AdminConstant.ROLE_USER, "USR", "User", userMenuSet, userPermissionSet, CommonConstant.STATUS_ACTIVE);
    }

    private void generateRoleItem(int id, String roleCode, String roleName, Set<AdminMenu> menuSet, Set<AdminPermission> permissionSet, int status) {
        AdminRole adminRole = adminRoleRepo.findById(id).orElseGet(() -> {
            AdminRole x = new AdminRole();
            x.setId(id);
            x.setRoleCode(roleCode);
            x.setRoleName(roleName);
            x.setStatus(status);
            x.setMenuSet(menuSet);
            x.setPermissionSet(permissionSet);

            return adminRoleRepo.save(x);
        });

        roles.put(id, adminRole);
    }
}
