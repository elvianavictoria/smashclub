package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.booking.model.Coach;
import com.backendsyndicate.smashclub.booking.repo.CoachRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@Transactional
@Profile("dev")
public class CoachSeeder implements DataSeeder {
    private CoachRepo coachRepo;

    public CoachSeeder(CoachRepo coachRepo) {
        this.coachRepo = coachRepo;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding coach data...");
        initCoach();
    }

    public void initCoach() {
        generateCoachItem(1L, "CCH-01", "Budi Santoso", BigDecimal.valueOf(200000), (byte) CommonConstant.STATUS_ACTIVE);
        generateCoachItem(2L, "CCH-02", "Aji Dumang", BigDecimal.valueOf(150000), (byte) CommonConstant.STATUS_ACTIVE);
    }

    private void generateCoachItem(Long id, String coachCode, String coachName, BigDecimal pricePerHour, byte status) {
        coachRepo.findById(id).orElseGet( () -> {
            Coach x = new Coach();
//            x.setId(id);
            x.setCoachCode(coachCode);
            x.setCoachName(coachName);
            x.setPricePerHour(pricePerHour);
            x.setStatus(status);

            return coachRepo.save(x);
        } );
    }
}
