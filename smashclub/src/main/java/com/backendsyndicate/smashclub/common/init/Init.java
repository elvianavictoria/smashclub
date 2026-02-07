package com.backendsyndicate.smashclub.common.init;

import com.backendsyndicate.smashclub.admin.init.AdminInitLoader;
import com.backendsyndicate.smashclub.payment.init.PaymentInitLoader;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// Put all methods to load static constants and seeders here
@Component
public class Init {
    @EventListener(ContextRefreshedEvent.class)
    public static void load() {
//        AdminInitLoader.load();
        PaymentInitLoader.load();
    }
}
