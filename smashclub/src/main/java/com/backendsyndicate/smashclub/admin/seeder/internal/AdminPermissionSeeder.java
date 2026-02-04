package com.backendsyndicate.smashclub.admin.seeder.internal;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.repo.AdminPermissionRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class AdminPermissionSeeder implements DataSeeder {
    private final AdminPermissionRepo adminPermissionRepo;
    private final AdminMenuSeeder adminMenuSeeder;

    @Getter
    private final Map<Integer, AdminPermission> permissions = new HashMap<>();

    public AdminPermissionSeeder(AdminPermissionRepo adminPermissionRepo, AdminMenuSeeder adminMenuSeeder) {
        this.adminPermissionRepo = adminPermissionRepo;
        this.adminMenuSeeder = adminMenuSeeder;
    }

    public void seed() {
        Logging.printConsole("Seeding permission data...");
        initPermission();
    }

    private void initPermission() {
        generatePermissionItem(AdminConstant.PERMISSION_DASHBOARD_READ, "View Dashboard", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_DASHBOARD));

        generatePermissionItem(AdminConstant.PERMISSION_COURT_READ, "View Court List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_CREATE, "Create Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_EDIT, "Edit Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_DELETE, "Delete Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));

        generatePermissionItem(AdminConstant.PERMISSION_COACH_READ, "View Coach List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_CREATE, "Create Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_EDIT, "Edit Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_DELETE, "Delete Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));

        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_READ, "View Equipment List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CREATE, "Create Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_EDIT, "Edit Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_DELETE, "Delete Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));

        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_READ, "View Product List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_CREATE, "Create Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_EDIT, "Edit Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_DELETE, "Delete Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));

        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_READ, "View Player List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_EDIT, "Edit Player", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_DELETE, "Delete Player", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));

        generatePermissionItem(AdminConstant.PERMISSION_SALES_READ, "View Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_SALES_DETAIL, "View Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_READ, "View Booking Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_DETAIL, "View Booking Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_READ, "View Product Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_DETAIL, "View Product Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_ROLES_READ, "View Roles List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_CREATE, "Create Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_EDIT, "Edit Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_DELETE, "Delete Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));

        generatePermissionItem(AdminConstant.PERMISSION_USERS_READ, "View User List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_CREATE, "Create User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_EDIT, "Edit User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_DELETE, "Delete User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
    }

    private void generatePermissionItem(int id, String permissionName, int status, AdminMenu menu) {
        AdminPermission adminPermission = adminPermissionRepo.findById(id).orElseGet( () -> {
            AdminPermission x = new AdminPermission();
            x.setId(id);
            x.setPermissionName(permissionName);
            x.setStatus(status);
            x.setMenu(menu);

            return adminPermissionRepo.save(x);
        } );

        permissions.put(id, adminPermission);
    }
}
