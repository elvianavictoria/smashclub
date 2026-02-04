package com.backendsyndicate.smashclub.admin.seeder;

import org.springframework.stereotype.Component;

//@Component
public class Seeder {
//    public static void load() {
//        MenuSeeder.init();
//        PermissionSeeder.init();
//        RoleSeeder.init();
//        UserSeeder.init();
//    }
    private final MenuSeeder menuSeeder;
    private final PermissionSeeder permissionSeeder;
    private final RoleSeeder roleSeeder;
    private final UserSeeder userSeeder;

    public Seeder(
            MenuSeeder menuSeeder,
            PermissionSeeder permissionSeeder,
            RoleSeeder roleSeeder,
            UserSeeder userSeeder
    ) {
        this.menuSeeder = menuSeeder;
        this.permissionSeeder = permissionSeeder;
        this.roleSeeder = roleSeeder;
        this.userSeeder = userSeeder;
    }

    public void load() {
        menuSeeder.init();
        permissionSeeder.init();
        roleSeeder.init();
        userSeeder.init();
    }
}
