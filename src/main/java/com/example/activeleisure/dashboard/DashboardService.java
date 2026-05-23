package com.example.activeleisure.dashboard;

import com.example.activeleisure.activity.ActivityRepository;
import com.example.activeleisure.booking.BookingRepository;
import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.common.Enums.EventStatus;
import com.example.activeleisure.common.Enums.PaymentStatus;
import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.AdminDashboardResponse;
import com.example.activeleisure.dto.ApiDtos.ManagerDashboardResponse;
import com.example.activeleisure.employee.EmployeeProfileRepository;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.notification.NotificationRepository;
import com.example.activeleisure.payment.PaymentRepository;
import com.example.activeleisure.schedule.EventScheduleRepository;
import com.example.activeleisure.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final EventScheduleRepository eventRepository;
    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeRepository;
    private final ActivityRepository activityRepository;
    private final NotificationRepository notificationRepository;
    private final DtoMapper mapper;

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ManagerDashboardResponse manager() {
        var bookings = bookingRepository.findAll();
        var paidRevenue = paymentRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> popular = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getEvent().getActivity().getTitle(), Collectors.counting()));
        Map<String, Long> workload = eventRepository.findAll().stream()
                .collect(Collectors.groupingBy(e -> e.getInstructor().getFullName(), Collectors.counting()));
        var nearestEvents = eventRepository.findTop5ByStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(
                EventStatus.PLANNED, LocalDateTime.now()).stream().map(mapper::event).toList();
        return new ManagerDashboardResponse(
                bookings.stream().filter(b -> b.getStatus() == BookingStatus.NEW).count(),
                bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count(),
                bookings.stream().filter(b -> b.getStatus() == BookingStatus.PAID).count(),
                paidRevenue,
                nearestEvents,
                popular,
                workload
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardResponse admin() {
        BigDecimal paidRevenue = paymentRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var recentActions = notificationRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(mapper::notification)
                .toList();
        return new AdminDashboardResponse(
                userRepository.count(),
                userRepository.findByRole(Role.CLIENT).size(),
                employeeRepository.count(),
                activityRepository.count(),
                bookingRepository.count(),
                paidRevenue,
                recentActions
        );
    }
}
