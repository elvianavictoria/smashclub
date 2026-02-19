package com.backendsyndicate.smashclub.admin.seeder;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.admin.seeder.app.UserSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminMenuSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminPermissionSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminRoleSeeder;
import com.backendsyndicate.smashclub.admin.seeder.internal.AdminUserSeeder;
import com.backendsyndicate.smashclub.admin.seeder.master.*;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DatabaseSeeder implements ApplicationRunner {
    private final Map<String, DataSeeder> seeders;

    public DatabaseSeeder(
            Map<String, DataSeeder> seeders
    ) {
        this.seeders = seeders;
    }

    @Override
    public void run(ApplicationArguments args) {
        int seededCount = 0;

        for( DataSeeder seeder: seeders.values() ) {
            if( seeder != null ) {
                seeder.seed();
                seededCount++;
            }
        }

        Logging.printConsole(String.format("Seeded %d seeders!", seededCount));
    }
}
