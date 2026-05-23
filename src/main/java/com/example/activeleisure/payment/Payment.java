package com.example.activeleisure.payment;

import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.common.BaseEntity;
import com.example.activeleisure.common.Enums.PaymentMethod;
import com.example.activeleisure.common.Enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;
    @Column(nullable = false, unique = true)
    private String mockTransactionId;
    private Instant paidAt;
}
