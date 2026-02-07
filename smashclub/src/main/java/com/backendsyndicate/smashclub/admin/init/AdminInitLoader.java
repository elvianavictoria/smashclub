package com.backendsyndicate.smashclub.admin.init;

import com.backendsyndicate.smashclub.admin.seeder.DatabaseSeeder;
import com.backendsyndicate.smashclub.common.util.Logging;

//@Component
public class AdminInitLoader {
    public static void load() {
        loadConstant();
    }

    private static void loadConstant() {
        Logging.printConsole("Loading admin constant variables");
    }
}
