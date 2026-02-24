package com.backendsyndicate.smashclub.admin.seeder.internal;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.repo.AdminPermissionRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
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
        generatePermissionItem(AdminConstant.PERMISSION_DASHBOARD_READ, PermissionConstant.DASHBOARD_READ_CODE, "View Dashboard", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_DASHBOARD));

        generatePermissionItem(AdminConstant.PERMISSION_COURT_READ, PermissionConstant.COURT_READ_CODE, "View Court List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_CREATE, PermissionConstant.COURT_CREATE_CODE, "Create Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_EDIT, PermissionConstant.COURT_EDIT_CODE, "Edit Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));
        generatePermissionItem(AdminConstant.PERMISSION_COURT_DELETE, PermissionConstant.COURT_DELETE_CODE, "Delete Court", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COURT));

        generatePermissionItem(AdminConstant.PERMISSION_COACH_READ, PermissionConstant.COACH_READ_CODE, "View Coach List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_CREATE, PermissionConstant.COACH_CREATE_CODE, "Create Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_EDIT, PermissionConstant.COACH_EDIT_CODE, "Edit Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));
        generatePermissionItem(AdminConstant.PERMISSION_COACH_DELETE, PermissionConstant.COACH_DELETE_CODE, "Delete Coach", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_COACH));

        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_READ, PermissionConstant.EQUIPMENT_CATEGORY_READ_CODE, "View Equipment Category List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_CREATE, PermissionConstant.EQUIPMENT_CATEGORY_CREATE_CODE, "Create Equipment Category", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_EDIT, PermissionConstant.EQUIPMENT_CATEGORY_EDIT_CODE, "Edit Equipment Category", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CATEGORY_DELETE, PermissionConstant.EQUIPMENT_CATEGORY_DELETE_CODE, "Delete Equipment Category", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT_CATEGORY));

        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_READ, PermissionConstant.EQUIPMENT_READ_CODE, "View Equipment List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_CREATE, PermissionConstant.EQUIPMENT_CREATE_CODE, "Create Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_EDIT, PermissionConstant.EQUIPMENT_EDIT_CODE, "Edit Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));
        generatePermissionItem(AdminConstant.PERMISSION_EQUIPMENT_DELETE, PermissionConstant.EQUIPMENT_DELETE_CODE, "Delete Equipment", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_EQUIPMENT));

        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_READ, PermissionConstant.PRODUCT_READ_CODE, "View Product List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_CREATE, PermissionConstant.PRODUCT_CREATE_CODE, "Create Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_EDIT, PermissionConstant.PRODUCT_EDIT_CODE, "Edit Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_DELETE, PermissionConstant.PRODUCT_DELETE_CODE, "Delete Product", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT));

        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_READ, PermissionConstant.PLAYER_READ_CODE, "View Player List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_EDIT, PermissionConstant.PLAYER_EDIT_CODE, "Edit Player", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));
        generatePermissionItem(AdminConstant.PERMISSION_PLAYER_DELETE, PermissionConstant.PLAYER_DELETE_CODE, "Delete Player", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PLAYER));

        generatePermissionItem(AdminConstant.PERMISSION_SALES_READ, PermissionConstant.SALES_READ_CODE, "View Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_SALES_DETAIL, PermissionConstant.SALES_DETAIL_CODE, "View Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_READ, PermissionConstant.BOOKING_SALES_READ_CODE, "View Booking Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_DETAIL, PermissionConstant.BOOKING_SALES_DETAIL_CODE, "View Booking Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_BOOKING_SALES_PROCESS, PermissionConstant.BOOKING_SALES_PROCESS_CODE, "Process Booking Sales", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_BOOKING_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_READ, PermissionConstant.PRODUCT_SALES_READ_CODE, "View Product Sales List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_DETAIL, PermissionConstant.PRODUCT_SALES_DETAIL_CODE, "View Product Sales Detail", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));
        generatePermissionItem(AdminConstant.PERMISSION_PRODUCT_SALES_PROCESS, PermissionConstant.PRODUCT_SALES_PROCESS_CODE, "Process Product Sales", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_PRODUCT_SALES));

        generatePermissionItem(AdminConstant.PERMISSION_REFUND_REQUEST_READ, PermissionConstant.REFUND_REQUEST_READ_CODE, "View Refund Request List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_REFUND_REQUEST));
        generatePermissionItem(AdminConstant.PERMISSION_REFUND_REQUEST_EDIT, PermissionConstant.REFUND_REQUEST_EDIT_CODE, "Edit Refund Request", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_REFUND_REQUEST));

        generatePermissionItem(AdminConstant.PERMISSION_ROLES_READ, PermissionConstant.ROLES_READ_CODE, "View Roles List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_CREATE, PermissionConstant.ROLES_CREATE_CODE, "Create Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_EDIT, PermissionConstant.ROLES_EDIT_CODE, "Edit Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));
        generatePermissionItem(AdminConstant.PERMISSION_ROLES_DELETE, PermissionConstant.ROLES_DELETE_CODE, "Delete Roles", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_ROLES));

        generatePermissionItem(AdminConstant.PERMISSION_USERS_READ, PermissionConstant.USERS_READ_CODE, "View User List", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_CREATE, PermissionConstant.USERS_CREATE_CODE, "Create User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_EDIT, PermissionConstant.USERS_EDIT_CODE, "Edit User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
        generatePermissionItem(AdminConstant.PERMISSION_USERS_DELETE, PermissionConstant.USERS_DELETE_CODE, "Delete User", CommonConstant.STATUS_ACTIVE, adminMenuSeeder.getMenus().get(AdminConstant.MENU_USER));
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
