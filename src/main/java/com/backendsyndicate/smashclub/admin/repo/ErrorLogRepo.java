package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepo extends JpaRepository<ErrorLog, Long> {
    
}
