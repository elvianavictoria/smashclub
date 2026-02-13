package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.repo.CourtRepo;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Profile("dev")
@Component
@Transactional
public class CourtSeeder implements DataSeeder {
    private CourtRepo courtRepo;

    public CourtSeeder(CourtRepo courtRepo) {
        this.courtRepo = courtRepo;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding court data...");
        initCourt();
    }

    public void initCourt() {
        generateCourtItem(1L, "CT01", "Court A", LocalTime.of(8, 0), LocalTime.of(23, 0), (byte) CommonConstant.STATUS_ACTIVE);
        generateCourtItem(2L, "CT02", "Court B", LocalTime.of(7, 0), LocalTime.of(20, 0), (byte) CommonConstant.STATUS_ACTIVE);
    }

    private void generateCourtItem(Long id, String courtCode, String courtName, LocalTime openTime, LocalTime closeTime, byte status) {
        courtRepo.findById(id).orElseGet( () -> {
            Court x = new Court();
//            x.setId(id);
            x.setCourtCode(courtCode);
            x.setCourtName(courtName);
            x.setOpenTime(openTime);
            x.setCloseTime(closeTime);
            x.setStatus(status);

            return courtRepo.save(x);
        } );
    }
}
