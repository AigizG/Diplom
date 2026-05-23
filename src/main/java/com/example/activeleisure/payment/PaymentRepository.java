package com.example.activeleisure.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingClientId(Long clientId);
    List<Payment> findByStatusAndPaidAtBetween(com.example.activeleisure.common.Enums.PaymentStatus status, Instant from, Instant to);
}
