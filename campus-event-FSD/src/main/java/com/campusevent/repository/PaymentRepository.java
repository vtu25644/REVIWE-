package com.campusevent.repository;

import com.campusevent.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRegistrationId(Long registrationId);
}
