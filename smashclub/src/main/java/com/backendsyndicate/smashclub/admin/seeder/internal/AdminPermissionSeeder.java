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
        generatePermissionItem(AdminConstant.PERMISSION_DASHBOARD_READ, AdminConstant.DASHBOARD_READ_CODE, "View Dashboard", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_DASHBOARD));

        generatePermissionItem(AdminConstant.PERMISSION_COURT_READ, AdminConstant.COURT_READ_CODE, "View Court List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_CREATE, AdminConstant.COURT_CREATE_CODE, "Create Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_EDIT, AdminConstant.COURT_EDIT_CODE, "Edit Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_DELETE, AdminConstant.COURT_DELETE_CODE, "Delete Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));

        generatePermissionItem(AdminConstant.PERMISSION_COACH_READ, AdminConstant.COACH_READ_CODE, "View Coach List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_CREATE, AdminConstant.COACH_CREATE_CODE, "Create Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_EDIT, AdminConstant.COACH_EDIT_CODE, "Edit Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_DELETE, AdminConstant.COACH_DELETE_CODE, "Delete Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));

        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_READ, AdminConstant.EQUIPMENT_CATEGORY_READ_CODE, "View Equipment Category List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_CREATE, AdminConstant.EQUIPMENT_CATEGORY_CREATE_CODE, "Create Equipment Category", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_EDIT, AdminConstant.EQUIPMENT_CATEGORY_EDIT_CODE, "Edit Equipment Category", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_DELETE, AdminConstant.EQUIPMENT_CATEGORY_DELETE_CODE, "Delete Equipment Category", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));

        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_READ, AdminConstant.EQUIPMENT_READ_CODE, "View Equipment List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CREATE, AdminConstant.EQUIPMENT_CREATE_CODE, "Create Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_EDIT, AdminConstant.EQUIPMENT_EDIT_CODE, "Edit Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_DELETE, AdminConstant.EQUIPMENT_DELETE_CODE, "Delete Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));

        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_READ, AdminConstant.PRODUCT_READ_CODE, "View Product List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_CREATE, AdminConstant.PRODUCT_CREATE_CODE, "Create Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_EDIT, AdminConstant.PRODUCT_EDIT_CODE, "Edit Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_DELETE, AdminConstant.PRODUCT_DELETE_CODE, "Delete Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));

        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_READ, AdminConstant.PLAYER_READ_CODE, "View Player List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_EDIT, AdminConstant.PLAYER_EDIT_CODE, "Edit Player", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_DELETE, AdminConstant.PLAYER_DELETE_CODE, "Delete Player", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));

        generatePermissionItem(AdminConstant.PERMISSION_SALES_READ, AdminConstant.SALES_READ_CODE, "View Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_SALES_DETAIL, AdminConstant.SALES_DETAIL_CODE, "View Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_READ, AdminConstant.BOOKING_SALES_READ_CODE, "View Booking Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_DETAIL, AdminConstant.BOOKING_SALES_DETAIL_CODE, "View Booking Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_READ, AdminConstant.PRODUCT_SALES_READ_CODE, "View Product Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_DETAIL, AdminConstant.PRODUCT_SALES_DETAIL_CODE, "View Product Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_ROLES_READ, AdminConstant.ROLES_READ_CODE, "View Roles List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_CREATE, AdminConstant.ROLES_CREATE_CODE, "Create Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_EDIT, AdminConstant.ROLES_EDIT_CODE, "Edit Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_DELETE, AdminConstant.ROLES_DELETE_CODE, "Delete Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));

        generatePermissionItem(AdminConstant.PERMISSION_USERS_READ, AdminConstant.USERS_READ_CODE, "View User List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_CREATE, AdminConstant.USERS_CREATE_CODE, "Create User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_EDIT, AdminConstant.USERS_EDIT_CODE, "Edit User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_DELETE, AdminConstant.USERS_DELETE_CODE, "Delete User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
    }

    private void generatePermissionItem(int id, String permissionCode, String permissionName, int status, AdminMenu menu) {
        AdminPermission adminPermission = adminPermissionRepo.findById(id).orElseGet( () -> {
            AdminPermission x = new AdminPermission();
            x.setId(id);
            x.setPermissionCode(permissionCode);
            x.setPermissionName(permissionName);
            x.setStatus(status);
            x.setMenu(menu);

            return adminPermissionRepo.save(x);
        } );

        permissions.put(id, adminPermission);
    }
}
