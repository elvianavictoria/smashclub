package com.backendsyndicate.smashclub.common.init;

import com.backendsyndicate.smashclub.admin.init.AdminInitLoader;
import com.backendsyndicate.smashclub.payment.init.PaymentInitLoader;

// Put all methods to load static constants and seeders here
public class Init {
    public static void load() {
        AdminInitLoader.load();
        PaymentInitLoader.load();
    }
}
