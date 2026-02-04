package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.repo.AdminPermissionRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//@Component
public class PermissionSeeder {
    @Autowired
    private final AdminPermissionRepo adminPermissionRepo;
    private final MenuSeeder menuSeeder;

    @Getter
    private final Map<Integer, AdminPermission> permissions = new HashMap<>();

    protected static final int PERMISSION_DASHBOARD_READ = 1;

    protected static final int PERMISSION_COURT_READ = 2;
    protected static final int PERMISSION_COURT_CREATE = 3;
    protected static final int PERMISSION_COURT_EDIT = 4;
    protected static final int PERMISSION_COURT_DELETE = 5;

    protected static final int PERMISSION_COACH_READ = 6;
    protected static final int PERMISSION_COACH_CREATE = 7;
    protected static final int PERMISSION_COACH_EDIT = 8;
    protected static final int PERMISSION_COACH_DELETE = 9;

    protected static final int PERMISSION_EQUIPMENT_READ = 10;
    protected static final int PERMISSION_EQUIPMENT_CREATE = 11;
    protected static final int PERMISSION_EQUIPMENT_EDIT = 12;
    protected static final int PERMISSION_EQUIPMENT_DELETE = 13;

    protected static final int PERMISSION_PRODUCT_READ = 14;
    protected static final int PERMISSION_PRODUCT_CREATE = 15;
    protected static final int PERMISSION_PRODUCT_EDIT = 16;
    protected static final int PERMISSION_PRODUCT_DELETE = 17;

    protected static final int PERMISSION_PLAYER_READ = 18;
    protected static final int PERMISSION_PLAYER_EDIT = 19;
    protected static final int PERMISSION_PLAYER_DELETE = 20;

    protected static final int PERMISSION_SALES_READ = 21;
    protected static final int PERMISSION_SALES_DETAIL = 22;

    protected static final int PERMISSION_BOOKING_SALES_READ = 23;
    protected static final int PERMISSION_BOOKING_SALES_DETAIL = 24;

    protected static final int PERMISSION_PRODUCT_SALES_READ = 25;
    protected static final int PERMISSION_PRODUCT_SALES_DETAIL = 26;

    protected static final int PERMISSION_ROLES_READ = 27;
    protected static final int PERMISSION_ROLES_CREATE = 28;
    protected static final int PERMISSION_ROLES_EDIT = 29;
    protected static final int PERMISSION_ROLES_DELETE = 30;

    protected static final int PERMISSION_USERS_READ = 31;
    protected static final int PERMISSION_USERS_CREATE = 32;
    protected static final int PERMISSION_USERS_EDIT = 33;
    protected static final int PERMISSION_USERS_DELETE = 34;

    public PermissionSeeder(AdminPermissionRepo adminPermissionRepo, MenuSeeder menuSeeder) {
        this.adminPermissionRepo = adminPermissionRepo;
        this.menuSeeder = menuSeeder;
        init();
    }

    protected void init() {
        initPermission();
    }

    private void initPermission() {
        generatePermissionItem(PERMISSION_DASHBOARD_READ, "View Dashboard", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_DASHBOARD));

        generatePermissionItem(PERMISSION_COURT_READ, "View Court List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COURT));
        generatePermissionItem(PERMISSION_COURT_CREATE, "Create Court", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COURT));
        generatePermissionItem(PERMISSION_COURT_EDIT, "Edit Court", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COURT));
        generatePermissionItem(PERMISSION_COURT_DELETE, "Delete Court", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COURT));

        generatePermissionItem(PERMISSION_COACH_READ, "View Coach List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COACH));
        generatePermissionItem(PERMISSION_COACH_CREATE, "Create Coach", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COACH));
        generatePermissionItem(PERMISSION_COACH_EDIT, "Edit Coach", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COACH));
        generatePermissionItem(PERMISSION_COACH_DELETE, "Delete Coach", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_COACH));

        generatePermissionItem(PERMISSION_EQUIPMENT_READ, "View Equipment List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_EQUIPMENT));
        generatePermissionItem(PERMISSION_EQUIPMENT_CREATE, "Create Equipment", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_EQUIPMENT));
        generatePermissionItem(PERMISSION_EQUIPMENT_EDIT, "Edit Equipment", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_EQUIPMENT));
        generatePermissionItem(PERMISSION_EQUIPMENT_DELETE, "Delete Equipment", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_EQUIPMENT));

        generatePermissionItem(PERMISSION_PRODUCT_READ, "View Product List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_PRODUCT));
        generatePermissionItem(PERMISSION_PRODUCT_CREATE, "Create Product", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_PRODUCT));
        generatePermissionItem(PERMISSION_PRODUCT_EDIT, "Edit Product", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_PRODUCT));
        generatePermissionItem(PERMISSION_PRODUCT_DELETE, "Delete Product", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_PRODUCT));

        generatePermissionItem(PERMISSION_SALES_READ, "View Sales List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_SALES));
        generatePermissionItem(PERMISSION_SALES_DETAIL, "View Sales Detail", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_SALES));

        generatePermissionItem(PERMISSION_BOOKING_SALES_READ, "View Booking Sales List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_BOOKING_SALES));
        generatePermissionItem(PERMISSION_BOOKING_SALES_DETAIL, "View Booking Sales Detail", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_BOOKING_SALES));

        generatePermissionItem(PERMISSION_PRODUCT_SALES_READ, "View Product Sales List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_PRODUCT_SALES));
        generatePermissionItem(PERMISSION_PRODUCT_SALES_DETAIL, "View Product Sales Detail", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_PRODUCT_SALES));

        generatePermissionItem(PERMISSION_ROLES_READ, "View Roles List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_ROLES));
        generatePermissionItem(PERMISSION_ROLES_CREATE, "Create Roles", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_ROLES));
        generatePermissionItem(PERMISSION_ROLES_EDIT, "Edit Roles", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_ROLES));
        generatePermissionItem(PERMISSION_ROLES_DELETE, "Delete Roles", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_ROLES));

        generatePermissionItem(PERMISSION_USERS_READ, "View User List", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_USER));
        generatePermissionItem(PERMISSION_USERS_CREATE, "Create User", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_USER));
        generatePermissionItem(PERMISSION_USERS_EDIT, "Edit User", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_USER));
        generatePermissionItem(PERMISSION_USERS_DELETE, "Delete User", CommonConstant.STATUS_ACTIVE, menuSeeder.getMenus().get(MenuSeeder.MENU_USER));
    }

    private void generatePermissionItem(int id, String permissionName, int status, AdminMenu menu) {
        Optional<AdminPermission> opt = adminPermissionRepo.findById(id);

        if( opt.isEmpty() ) {
            AdminPermission adminPermission = new AdminPermission();
            adminPermission.setId(id);
            adminPermission.setPermissionName(permissionName);
            adminPermission.setStatus(status);
            adminPermission.setMenu(menu);

            permissions.put(id, adminPermission);
            adminPermissionRepo.save(adminPermission);
        }

    }
}
