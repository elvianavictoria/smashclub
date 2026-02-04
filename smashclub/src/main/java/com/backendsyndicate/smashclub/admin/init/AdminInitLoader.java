package com.backendsyndicate.smashclub.admin.init;

import com.backendsyndicate.smashclub.admin.seeder.Seeder;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.stereotype.Component;

//@Component
public class AdminInitLoader {
//    public static void load() {
//        loadConstant();
//        loadSeeder();
//    }
//
//    private static void loadSeeder() {
//        Seeder.load();
//    }
//
//    private static void loadConstant() {
//        Logging.printConsole("Loading all constant variables");
//
//    }
    private final Seeder seeder;

    public AdminInitLoader(Seeder seeder) {
        this.seeder = seeder;
    }

    public void load() {
        loadConstant();
        loadSeeder();
    }

    private void loadSeeder() {
        seeder.load();
    }

    private void loadConstant() {
        Logging.printConsole("Loading all constant variables");
    }
}
