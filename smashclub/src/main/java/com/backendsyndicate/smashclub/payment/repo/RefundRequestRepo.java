package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRequestRepo extends JpaRepository<RefundRequest, Long> {

}
