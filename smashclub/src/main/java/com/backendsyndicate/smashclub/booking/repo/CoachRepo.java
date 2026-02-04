package com.backendsyndicate.smashclub.booking.repo;

import com.backendsyndicate.smashclub.booking.model.Coach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepo extends JpaRepository<Coach, Long> {
    Page<Coach> findAllByCoachCodeContainsOrCoachNameContains(String coachCode, String coachName, Pageable pageable);
}
