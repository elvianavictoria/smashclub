package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminMenuCategory;
import com.backendsyndicate.smashclub.admin.repo.AdminMenuCategoryRepo;
import com.backendsyndicate.smashclub.admin.repo.AdminMenuRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class MenuSeeder {
    @Autowired
    private static AdminMenuRepo adminMenuRepo;
    @Autowired
    private static AdminMenuCategoryRepo adminMenuCategoryRepo;

    private static List<AdminMenuCategory> categories = new ArrayList<>();

    public static void init() {
        initMenuCategory();
        initMenu();
    }

    private static void initMenuCategory() {
        categories.add(generateCategoryItem("", 1));
        categories.add(generateCategoryItem("Master Data", 1));
        categories.add(generateCategoryItem("Laporan", 1));
        categories.add(generateCategoryItem("Manajemen Pengguna", 1));
    }

    private static void initMenu() {
        generateMenuItem(categories.get(0), "home", "Dashboard", 1, 0);

        // Master Data
        generateMenuItem(categories.get(1), "court", "Lapangan", 1, 0);
        generateMenuItem(categories.get(1), "coach", "Pelatih", 1, 0);
        generateMenuItem(categories.get(1), "equipment", "Peralatan", 1, 0);
        generateMenuItem(categories.get(1), "product", "Produk", 1, 0);

        // Report
        generateMenuItem(categories.get(2), "player", "Data Pemain", 1, 0);
        generateMenuItem(categories.get(2), "sales", "Penjualan", 1, 0);
        generateMenuItem(categories.get(2), "court-booking", "Pemesanan Lapangan", 1, 0);
        generateMenuItem(categories.get(2), "product", "Pemesanan Produk", 1, 0);

        // Users Management
        generateMenuItem(categories.get(3), "role", "Peran", 1, 0);
        generateMenuItem(categories.get(3), "user", "Pengguna", 1, 0);
    }

    private static AdminMenuCategory generateCategoryItem(String categoryName, int status) {
        AdminMenuCategory category = new AdminMenuCategory();
        category.setCategoryName(categoryName);
        category.setStatus(status);
        adminMenuCategoryRepo.save(category);

        return category;
    }

    private static void generateMenuItem(AdminMenuCategory category, String menuCode, String menuName, int status, int parentId) {
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.setCategory(category);
        adminMenu.setMenuCode(menuCode);
        adminMenu.setMenuName(menuName);
        adminMenu.setStatus(status);
        adminMenu.setParentId(parentId);

        adminMenuRepo.save(adminMenu);
    }
}
