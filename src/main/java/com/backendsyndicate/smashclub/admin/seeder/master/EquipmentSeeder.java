package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.booking.model.Equipment;
import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import com.backendsyndicate.smashclub.booking.repo.EquipmentRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Profile({"dev", "staging"})
@Component
@Transactional
public class EquipmentSeeder implements DataSeeder {
    private EquipmentRepo equipmentRepo;
    private EquipmentCategorySeeder equipmentCategorySeeder;

    public EquipmentSeeder(EquipmentRepo equipmentRepo, EquipmentCategorySeeder equipmentCategorySeeder) {
        this.equipmentRepo = equipmentRepo;
        this.equipmentCategorySeeder = equipmentCategorySeeder;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding equipment data...");
        initEquipment();
    }

    public void initEquipment() {
        generateEquipmentItem(1L, "Raket A", "Wilson", "Power Rackets", BigDecimal.valueOf(80000), 10, "Power Rackets Wilson head size 100\"", (byte) CommonConstant.STATUS_ACTIVE, equipmentCategorySeeder.getCategories().get(AdminConstant.EQUIPMENT_CATEGORY_RACQUET));
        generateEquipmentItem(2L, "Raket B", "Yonex", "Control/Player Rackets", BigDecimal.valueOf(120000), 5, "Control/Player Rackets Yonex head size 98\"", (byte) CommonConstant.STATUS_ACTIVE, equipmentCategorySeeder.getCategories().get(AdminConstant.EQUIPMENT_CATEGORY_RACQUET));
        generateEquipmentItem(3L, "Bola A", "Wilson", "Pressurized Ball", BigDecimal.valueOf(65000), 100, "Pressurized Balls by Wilson", (byte) CommonConstant.STATUS_ACTIVE, equipmentCategorySeeder.getCategories().get(AdminConstant.EQUIPMENT_CATEGORY_BALL));
        generateEquipmentItem(4L, "Bola B", "Yonex", "Pressureless Ball", BigDecimal.valueOf(50000), 80, "Pressurized Balls by Wilson", (byte) CommonConstant.STATUS_ACTIVE, equipmentCategorySeeder.getCategories().get(AdminConstant.EQUIPMENT_CATEGORY_BALL));
    }

    private void generateEquipmentItem(Long id, String equipmentName, String brand, String type, BigDecimal price, int stock, String description, int status, EquipmentCategory equipmentCategory) {
        equipmentRepo.findById(id).orElseGet( () -> {
            Equipment x = new Equipment();
//            x.setId(id);
            x.setEquipmentName(equipmentName);
            x.setBrand(brand);
            x.setType(type);
            x.setPrice(price);
            x.setStock(stock);
            x.setDescription(description);
            x.setStatus((byte)status);
            x.setEquipmentCategory(equipmentCategory);

            return equipmentRepo.save(x);
        } );
    }
}
