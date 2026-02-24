package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.booking.repository.CourtRepository;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@Transactional
@Profile({"dev", "staging"})
public class CourtSeeder implements DataSeeder {
    private CourtRepository courtRepo;

    public CourtSeeder(CourtRepository courtRepo) {
        this.courtRepo = courtRepo;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding court data...");
        initCourt();
    }

    public void initCourt() {
        generateCourtItem(1L, "CT01", "Court A", LocalTime.of(8, 0), LocalTime.of(23, 0), BigDecimal.valueOf(500000.0), (byte) CommonConstant.STATUS_ACTIVE, "https://res.cloudinary.com/dsao23xch/image/upload/v1771591910/tennis-court.jpg");
        generateCourtItem(2L, "CT02", "Court B", LocalTime.of(7, 0), LocalTime.of(20, 0), BigDecimal.valueOf(600000.0), (byte) CommonConstant.STATUS_ACTIVE, "https://res.cloudinary.com/dsao23xch/image/upload/v1771591926/tennis-court-2.jpg");
    }

    private void generateCourtItem(Long id, String courtCode, String courtName, LocalTime openTime, LocalTime closeTime, BigDecimal pricePerHour, byte status, String courtImgLink) {
        courtRepo.findById(id).orElseGet( () -> {
            Court x = new Court();
//            x.setId(id);
            x.setCourtCode(courtCode);
            x.setCourtName(courtName);
            x.setOpenTime(openTime);
            x.setCloseTime(closeTime);
            x.setPricePerHour(pricePerHour);
            x.setStatus(status);
            x.setCourtImgLink(courtImgLink);

            return courtRepo.save(x);
        } );
    }
}
