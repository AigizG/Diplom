package com.example.activeleisure.report;

import com.example.activeleisure.booking.BookingRepository;
import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.common.Enums.PaymentStatus;
import com.example.activeleisure.dto.ApiDtos.ReportResponse;
import com.example.activeleisure.equipment.EquipmentAssignmentRepository;
import com.example.activeleisure.payment.PaymentRepository;
import com.example.activeleisure.schedule.EventScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class ReportService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final EventScheduleRepository eventRepository;
    private final EquipmentAssignmentRepository assignmentRepository;

    public ReportResponse bookings(LocalDate from, LocalDate to) {
        long count = bookingRepository.findAll().stream().filter(b -> inRange(b.getCreatedAt(), from, to)).count();
        return new ReportResponse("bookings", Map.of("count", count));
    }

    public ReportResponse revenue(LocalDate from, LocalDate to) {
        BigDecimal amount = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .filter(p -> p.getPaidAt() != null && inRange(p.getPaidAt(), from, to))
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResponse("revenue", Map.of("paidAmount", amount));
    }

    public ReportResponse popularActivities(LocalDate from, LocalDate to) {
        Map<String, Long> data = bookingRepository.findAll().stream()
                .filter(b -> inRange(b.getCreatedAt(), from, to))
                .collect(Collectors.groupingBy(b -> b.getEvent().getActivity().getTitle(), Collectors.counting()));
        return new ReportResponse("popular-activities", data);
    }

    public ReportResponse instructorWorkload(LocalDate from, LocalDate to) {
        Map<String, Long> data = eventRepository.findAll().stream()
                .filter(e -> inRange(e.getCreatedAt(), from, to))
                .collect(Collectors.groupingBy(e -> e.getInstructor().getEmail(), Collectors.counting()));
        return new ReportResponse("instructor-workload", data);
    }

    public ReportResponse equipmentUsage(LocalDate from, LocalDate to) {
        Map<String, Integer> data = assignmentRepository.findAll().stream()
                .filter(a -> inRange(a.getAssignedAt(), from, to))
                .collect(Collectors.groupingBy(a -> a.getEquipment().getName(), Collectors.summingInt(a -> a.getQuantity())));
        return new ReportResponse("equipment-usage", data);
    }

    public ReportResponse cancellations(LocalDate from, LocalDate to) {
        long count = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .filter(b -> inRange(b.getUpdatedAt(), from, to))
                .count();
        return new ReportResponse("cancellations", Map.of("count", count));
    }

    private boolean inRange(java.time.Instant instant, LocalDate from, LocalDate to) {
        if (instant == null) return false;
        LocalDate date = instant.atZone(ZoneOffset.UTC).toLocalDate();
        return (from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to));
    }
}
