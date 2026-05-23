package com.example.activeleisure.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingParticipantRepository extends JpaRepository<BookingParticipant, Long> {
    List<BookingParticipant> findByBookingId(Long bookingId);
}
