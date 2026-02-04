package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.seeder.internal.AdminMenuSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminPermissionSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminRoleSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminUserSeeder;
import com.backendsyndicate.smashclub.admin.seeder.master.*;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements ApplicationRunner {
    private final AdminMenuSeeder adminMenuSeeder;
    private final AdminPermissionSeeder adminPermissionSeeder;
    private final AdminRoleSeeder adminRoleSeeder;
    private final AdminUserSeeder adminUserSeeder;

    private final CourtSeeder courtSeeder;
    private final CoachSeeder coachSeeder;
    private final EquipmentCategorySeeder equipmentCategorySeeder;
    private final EquipmentSeeder equipmentSeeder;
    private final ProductSeeder productSeeder;
    private final ProductVariantSeeder productVariantSeeder;

    public DatabaseSeeder(
            AdminMenuSeeder adminMenuSeeder,
            AdminPermissionSeeder adminPermissionSeeder,
            AdminRoleSeeder adminRoleSeeder,
            AdminUserSeeder adminUserSeeder,
            CourtSeeder courtSeeder,
            CoachSeeder coachSeeder,
            EquipmentCategorySeeder equipmentCategorySeeder,
            EquipmentSeeder equipmentSeeder,
            ProductSeeder productSeeder,
            ProductVariantSeeder productVariantSeeder
    ) {
        this.adminMenuSeeder = adminMenuSeeder;
        this.adminPermissionSeeder = adminPermissionSeeder;
        this.adminRoleSeeder = adminRoleSeeder;
        this.adminUserSeeder = adminUserSeeder;

        this.courtSeeder = courtSeeder;
        this.coachSeeder = coachSeeder;
        this.equipmentCategorySeeder = equipmentCategorySeeder;
        this.equipmentSeeder = equipmentSeeder;
        this.productSeeder = productSeeder;
        this.productVariantSeeder = productVariantSeeder;
    }

    @Override
    public void run(ApplicationArguments args) {
        adminMenuSeeder.seed();
        adminPermissionSeeder.seed();
        adminRoleSeeder.seed();
        adminUserSeeder.seed();

        courtSeeder.seed();
        coachSeeder.seed();
        equipmentCategorySeeder.seed();
        equipmentSeeder.seed();
        productSeeder.seed();
        productVariantSeeder.seed();

        Logging.printConsole("Seeded 10 seeders!");
    }
}
