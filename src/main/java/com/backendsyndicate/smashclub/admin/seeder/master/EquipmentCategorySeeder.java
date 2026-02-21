package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import com.backendsyndicate.smashclub.booking.repository.EquipmentCategoryRepository;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class EquipmentCategorySeeder implements DataSeeder {
    private EquipmentCategoryRepository equipmentCategoryRepo;
    @Getter
    private final Map<Long, EquipmentCategory> categories = new HashMap<Long, EquipmentCategory>();

    public EquipmentCategorySeeder(EquipmentCategoryRepository equipmentCategoryRepo) {
        this.equipmentCategoryRepo = equipmentCategoryRepo;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding equipment category data...");
        initEquipmentCategory();
    }

    public void initEquipmentCategory() {
        generateEquipmentCategoryItem(AdminConstant.EQUIPMENT_CATEGORY_RACQUET, "Raket", (byte) CommonConstant.STATUS_ACTIVE);
        generateEquipmentCategoryItem(AdminConstant.EQUIPMENT_CATEGORY_BALL, "Bola", (byte) CommonConstant.STATUS_ACTIVE);
    }

    private void generateEquipmentCategoryItem(Long id, String categoryName, byte status) {
        EquipmentCategory category = equipmentCategoryRepo.findById(id).orElseGet( () -> {
            EquipmentCategory x = new EquipmentCategory();
//            x.setId(id);
            x.setCategoryName(categoryName);
            x.setStatus(status);

            return equipmentCategoryRepo.save(x);
        } );

        categories.put(id, category);
    }
}
