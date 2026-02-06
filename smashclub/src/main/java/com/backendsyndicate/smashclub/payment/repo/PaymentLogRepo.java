package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepo extends JpaRepository<PaymentLog, Long> {

}
