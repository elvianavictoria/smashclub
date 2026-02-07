package com.backendsyndicate.smashclub.booking.repo;

import com.backendsyndicate.smashclub.booking.model.Court;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepo extends JpaRepository<Court, Long> {
    Page<Court> findAllByCourtCodeContainsOrCourtNameContainsIgnoreCase(String courtCode, String courtName, Pageable pageable);
}
