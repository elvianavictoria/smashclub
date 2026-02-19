package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepo extends JpaRepository<ActivityLog, Long> {
    
}
