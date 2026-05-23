package com.example.activeleisure.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClientId(Long clientId);
    List<Booking> findByEventInstructorId(Long instructorId);
    List<Booking> findByEventId(Long eventId);
}
