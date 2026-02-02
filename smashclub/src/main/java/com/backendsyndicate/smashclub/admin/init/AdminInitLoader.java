package com.backendsyndicate.smashclub.admin.init;

import com.backendsyndicate.smashclub.admin.seeder.MenuSeeder;
import com.backendsyndicate.smashclub.common.util.Logging;

public class AdminInitLoader {
    public static void load() {
        loadSeeder();
        loadConstant();
    }

    private static void loadSeeder() {
        MenuSeeder.init();
    }

    private static void loadConstant() {
        Logging.printConsole("Loading all constant variables");


    }
}
