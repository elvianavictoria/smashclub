package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminMenuCategory;
import com.backendsyndicate.smashclub.admin.repo.AdminMenuCategoryRepo;
import com.backendsyndicate.smashclub.admin.repo.AdminMenuRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class MenuSeeder {
    @Autowired
    private final AdminMenuRepo adminMenuRepo;
    @Autowired
    private final AdminMenuCategoryRepo adminMenuCategoryRepo;

    @Getter
    private final Map<Integer, AdminMenuCategory> categories = new HashMap();
    @Getter
    private final Map<Integer, AdminMenu> menus = new HashMap<>();

    protected static final int MENU_CATEGORY_DASHBOARD = 1;
    protected static final int MENU_CATEGORY_MASTER_DATA = 2;
    protected static final int MENU_CATEGORY_REPORT = 3;
    protected static final int MENU_CATEGORY_USER = 4;

    protected static final int MENU_DASHBOARD = 1;
    protected static final int MENU_COURT = 2;
    protected static final int MENU_COACH = 3;
    protected static final int MENU_EQUIPMENT = 4;
    protected static final int MENU_PRODUCT = 5;
    protected static final int MENU_PLAYER = 6;
    protected static final int MENU_SALES = 7;
    protected static final int MENU_BOOKING_SALES = 8;
    protected static final int MENU_PRODUCT_SALES = 9;
    protected static final int MENU_ROLES = 10;
    protected static final int MENU_USER = 11;

    public MenuSeeder(
            AdminMenuRepo adminMenuRepo,
            AdminMenuCategoryRepo adminMenuCategoryRepo
    ) {
        this.adminMenuRepo = adminMenuRepo;
        this.adminMenuCategoryRepo = adminMenuCategoryRepo;
        init();
    }

    protected void init() {
        initMenuCategory();
        initMenu();
    }

    private void initMenuCategory() {
        generateCategoryItem(MENU_CATEGORY_DASHBOARD, "", CommonConstant.STATUS_ACTIVE);
        generateCategoryItem(MENU_CATEGORY_MASTER_DATA, "Master Data", CommonConstant.STATUS_ACTIVE);
        generateCategoryItem(MENU_CATEGORY_REPORT, "Laporan", CommonConstant.STATUS_ACTIVE);
        generateCategoryItem(MENU_CATEGORY_USER, "Manajemen Pengguna", CommonConstant.STATUS_ACTIVE);
    }

    private void initMenu() {
        generateMenuItem(categories.get(MENU_CATEGORY_DASHBOARD), MENU_DASHBOARD, "home", "Dashboard", CommonConstant.STATUS_ACTIVE, 0);

        // Master Data
        generateMenuItem(categories.get(MENU_CATEGORY_MASTER_DATA), MENU_COURT, "court", "Lapangan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_MASTER_DATA), MENU_COACH, "coach", "Pelatih", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_MASTER_DATA), MENU_EQUIPMENT, "equipment", "Peralatan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_MASTER_DATA), MENU_PRODUCT, "product", "Produk", CommonConstant.STATUS_ACTIVE, 0);

        // Report
        generateMenuItem(categories.get(MENU_CATEGORY_REPORT), MENU_PLAYER, "player", "Data Pemain", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_REPORT), MENU_SALES, "sales", "Penjualan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_REPORT), MENU_BOOKING_SALES, "court-booking", "Pemesanan Lapangan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_REPORT), MENU_PRODUCT_SALES, "product-sales", "Pemesanan Produk", CommonConstant.STATUS_ACTIVE, 0);

        // Users Management
        generateMenuItem(categories.get(MENU_CATEGORY_USER), MENU_ROLES, "role", "Peran", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(MENU_CATEGORY_USER), MENU_USER, "user", "Pengguna", CommonConstant.STATUS_ACTIVE, 0);
    }

    private void generateCategoryItem(int id, String categoryName, int status) {
        AdminMenuCategory category = new AdminMenuCategory();
        category.setId(id);
        category.setCategoryName(categoryName);
        category.setStatus(status);

        categories.put(id, category);
        adminMenuCategoryRepo.save(category);
    }

    private void generateMenuItem(AdminMenuCategory category, int id, String menuCode, String menuName, int status, int parentId) {
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.setId(id);
        adminMenu.setCategory(category);
        adminMenu.setMenuCode(menuCode);
        adminMenu.setMenuName(menuName);
        adminMenu.setStatus(status);
        adminMenu.setParentId(parentId);

        menus.put(id, adminMenu);
        adminMenuRepo.save(adminMenu);
    }
}
