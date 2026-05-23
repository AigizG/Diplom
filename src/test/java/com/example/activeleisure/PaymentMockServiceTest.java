package com.example.activeleisure;

import com.example.activeleisure.activity.Activity;
import com.example.activeleisure.activity.ActivityCategory;
import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.booking.BookingService;
import com.example.activeleisure.common.Enums.*;
import com.example.activeleisure.dto.ApiDtos.PaymentCreateRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.notification.NotificationService;
import com.example.activeleisure.payment.*;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.security.CurrentUserService;
import com.example.activeleisure.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentMockServiceTest {
    @Test
    void createsMockPaymentAndMarksPaid() {
        Booking booking = booking();
        BookingService bookings = mock(BookingService.class);
        when(bookings.get(1L)).thenReturn(booking);
        CurrentUserService current = mock(CurrentUserService.class);
        when(current.get()).thenReturn(booking.getClient());
        PaymentRepository payments = mock(PaymentRepository.class);
        when(payments.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(2L);
            return p;
        });

        PaymentService service = new PaymentService(payments, bookings, current, mock(NotificationService.class), new DtoMapper());
        var created = service.createMock(1L, new PaymentCreateRequest(PaymentMethod.CARD_MOCK));
        assertTrue(created.mockTransactionId().startsWith("MOCK-"));

        Payment payment = new Payment();
        payment.setId(2L);
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(PaymentMethod.CARD_MOCK);
        payment.setMockTransactionId(created.mockTransactionId());
        when(payments.findById(2L)).thenReturn(Optional.of(payment));

        var paid = service.paid(2L);
        assertEquals(PaymentStatus.PAID, paid.status());
        assertEquals(BookingStatus.PAID, booking.getStatus());
    }

    private Booking booking() {
        User client = new User();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setFullName("Client");
        client.setRole(Role.CLIENT);
        ActivityCategory category = new ActivityCategory();
        category.setId(1L);
        category.setName("Hiking");
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setTitle("Tour");
        activity.setDescription("Desc");
        activity.setCategory(category);
        activity.setPrice(new BigDecimal("100.00"));
        EventSchedule event = new EventSchedule();
        event.setId(1L);
        event.setActivity(activity);
        event.setInstructor(client);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setClient(client);
        booking.setEvent(event);
        booking.setParticipantsCount(1);
        booking.setTotalPrice(new BigDecimal("100.00"));
        return booking;
    }
}
