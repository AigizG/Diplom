package com.example.activeleisure.payment;

import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.booking.BookingService;
import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.common.Enums.NotificationType;
import com.example.activeleisure.common.Enums.PaymentStatus;
import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.PaymentCreateRequest;
import com.example.activeleisure.dto.ApiDtos.PaymentResponse;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.notification.NotificationService;
import com.example.activeleisure.security.CurrentUserService;
import com.example.activeleisure.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final DtoMapper mapper;

    @Transactional
    @PreAuthorize("hasAnyRole('CLIENT','MANAGER','ADMIN')")
    public PaymentResponse createMock(Long bookingId, PaymentCreateRequest request) {
        Booking booking = bookingService.get(bookingId);
        User current = currentUserService.get();
        if (current.getRole() == Role.CLIENT && !booking.getClient().getId().equals(current.getId())) {
            throw new AccessDeniedException("Бронирование принадлежит другому клиенту");
        }
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(request.method());
        payment.setMockTransactionId("MOCK-" + UUID.randomUUID());
        Payment saved = paymentRepository.save(payment);
        notificationService.notify(booking.getClient(), "Тестовая оплата создана",
                "Тестовая оплата №" + saved.getId() + " создана", NotificationType.PAYMENT_STATUS_CHANGED);
        return mapper.payment(saved);
    }

    @PreAuthorize("hasAnyRole('CLIENT','MANAGER','ADMIN')")
    public List<PaymentResponse> all() {
        User current = currentUserService.get();
        if (current.getRole() == Role.CLIENT) {
            return paymentRepository.findByBookingClientId(current.getId()).stream().map(mapper::payment).toList();
        }
        return paymentRepository.findAll().stream().map(mapper::payment).toList();
    }

    @PreAuthorize("hasRole('CLIENT')")
    public List<PaymentResponse> my() {
        return paymentRepository.findByBookingClientId(currentUserService.get().getId()).stream().map(mapper::payment).toList();
    }

    public PaymentResponse one(Long id) {
        Payment payment = get(id);
        assertCanSee(payment);
        return mapper.payment(payment);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PaymentResponse paid(Long id) {
        Payment payment = get(id);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(Instant.now());
        payment.getBooking().setStatus(BookingStatus.PAID);
        notify(payment);
        return mapper.payment(payment);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PaymentResponse partial(Long id) {
        Payment payment = get(id);
        payment.setStatus(PaymentStatus.PARTIALLY_PAID);
        notify(payment);
        return mapper.payment(payment);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PaymentResponse refund(Long id) {
        Payment payment = get(id);
        payment.setStatus(PaymentStatus.REFUNDED);
        Booking booking = payment.getBooking();
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            bookingService.cancelInternal(booking);
        }
        notify(payment);
        return mapper.payment(payment);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PaymentResponse cancel(Long id) {
        Payment payment = get(id);
        payment.setStatus(PaymentStatus.CANCELLED);
        notify(payment);
        return mapper.payment(payment);
    }

    public Payment get(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Оплата не найдена"));
    }

    private void assertCanSee(Payment payment) {
        User current = currentUserService.get();
        if (current.getRole() == Role.ADMIN || current.getRole() == Role.MANAGER) return;
        if (payment.getBooking().getClient().getId().equals(current.getId())) return;
        throw new AccessDeniedException("У вас нет доступа к этой оплате");
    }

    private void notify(Payment payment) {
        notificationService.notify(payment.getBooking().getClient(), "Статус тестовой оплаты изменён",
                "Статус оплаты №" + payment.getId() + ": " + payment.getStatus(),
                NotificationType.PAYMENT_STATUS_CHANGED);
    }
}
