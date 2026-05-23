package com.example.activeleisure.booking;

import com.example.activeleisure.common.BaseEntity;
import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.user.User;
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

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventSchedule event;
    @Column(nullable = false)
    private Integer participantsCount;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.NEW;
    private String comment;
}
