package com.backendsyndicate.smashclub.admin.seeder.internal;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminMenuCategory;
import com.backendsyndicate.smashclub.admin.repo.AdminMenuCategoryRepo;
import com.backendsyndicate.smashclub.admin.repo.AdminMenuRepo;
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
public class AdminMenuSeeder implements DataSeeder {
    private final AdminMenuCategoryRepo adminMenuCategoryRepo;
    private final AdminMenuRepo adminMenuRepo;

    @Getter
    private final Map<Integer, AdminMenuCategory> categories = new HashMap();
    @Getter
    private final Map<Integer, AdminMenu> menus = new HashMap<>();

    public AdminMenuSeeder(
            AdminMenuCategoryRepo adminMenuCategoryRepo,
            AdminMenuRepo adminMenuRepo
    ) {
        this.adminMenuCategoryRepo = adminMenuCategoryRepo;
        this.adminMenuRepo = adminMenuRepo;
    }

    public void seed() {
        Logging.printConsole("Seeding menu data...");
        initMenuCategory();
        initMenu();
    }

    private void initMenuCategory() {
        generateCategoryItem(AdminConstant.MENU_CATEGORY_DASHBOARD, "", CommonConstant.STATUS_ACTIVE);
        generateCategoryItem(AdminConstant.MENU_CATEGORY_MASTER_DATA, "Master Data", CommonConstant.STATUS_ACTIVE);
        generateCategoryItem(AdminConstant.MENU_CATEGORY_REPORT, "Laporan", CommonConstant.STATUS_ACTIVE);
        generateCategoryItem(AdminConstant.MENU_CATEGORY_USER, "Manajemen Pengguna", CommonConstant.STATUS_ACTIVE);
    }

    private void initMenu() {
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_DASHBOARD), AdminConstant.MENU_DASHBOARD, "home", "Dashboard", CommonConstant.STATUS_ACTIVE, 0);

        // Master Data
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_MASTER_DATA), AdminConstant.MENU_COURT, "court", "Lapangan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_MASTER_DATA), AdminConstant.MENU_COACH, "coach", "Pelatih", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_MASTER_DATA), AdminConstant.MENU_EQUIPMENT, "equipment", "Peralatan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_MASTER_DATA), AdminConstant.MENU_PRODUCT, "product", "Produk", CommonConstant.STATUS_ACTIVE, 0);

        // Report
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_REPORT), AdminConstant.MENU_PLAYER, "player", "Data Pemain", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_REPORT), AdminConstant.MENU_SALES, "sales", "Penjualan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_REPORT), AdminConstant.MENU_BOOKING_SALES, "court-booking", "Pemesanan Lapangan", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_REPORT), AdminConstant.MENU_PRODUCT_SALES, "product-sales", "Pemesanan Produk", CommonConstant.STATUS_ACTIVE, 0);

        // Users Management
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_USER), AdminConstant.MENU_ROLES, "role", "Peran", CommonConstant.STATUS_ACTIVE, 0);
        generateMenuItem(categories.get(AdminConstant.MENU_CATEGORY_USER), AdminConstant.MENU_USER, "user", "Pengguna", CommonConstant.STATUS_ACTIVE, 0);
    }

    private void generateCategoryItem(int id, String categoryName, int status) {
        AdminMenuCategory category = adminMenuCategoryRepo.findById(id).orElseGet(() -> {
            AdminMenuCategory x = new AdminMenuCategory();
            x.setId(id);
            x.setCategoryName(categoryName);
            x.setStatus(status);
            return adminMenuCategoryRepo.save(x);
        });

        categories.put(id, category);
    }

    private void generateMenuItem(AdminMenuCategory category, int id, String menuCode, String menuName, int status, int parentId) {
        AdminMenu adminMenu = adminMenuRepo.findById(id).orElseGet(() -> {
            AdminMenu x = new AdminMenu();
            x.setId(id);
            x.setCategory(category);
            x.setMenuCode(menuCode);
            x.setMenuName(menuName);
            x.setStatus(status);
            x.setParentId(parentId);

            return adminMenuRepo.save(x);
        });

        menus.put(id, adminMenu);
    }
}
