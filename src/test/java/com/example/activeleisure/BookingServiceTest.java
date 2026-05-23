package com.example.activeleisure;

import com.example.activeleisure.activity.Activity;
import com.example.activeleisure.activity.ActivityCategory;
import com.example.activeleisure.booking.*;
import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.BookingRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.notification.NotificationService;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventService;
import com.example.activeleisure.security.CurrentUserService;
import com.example.activeleisure.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {
    @Test
    void createsBookingAndDecreasesPlaces() {
        EventSchedule event = event(5);
        User client = user(Role.CLIENT);
        EventService events = mock(EventService.class);
        when(events.get(1L)).thenReturn(event);
        CurrentUserService current = mock(CurrentUserService.class);
        when(current.get()).thenReturn(client);
        BookingRepository bookings = mock(BookingRepository.class);
        when(bookings.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingService service = new BookingService(bookings, mock(BookingParticipantRepository.class), events,
                current, mock(NotificationService.class), new DtoMapper());
        var response = service.create(new BookingRequest(1L, 2, "ok", List.of()));

        assertEquals(3, event.getAvailablePlaces());
        assertEquals(new BigDecimal("200.00"), response.totalPrice());
    }

    @Test
    void rejectsBookingWithoutPlaces() {
        EventSchedule event = event(1);
        EventService events = mock(EventService.class);
        when(events.get(1L)).thenReturn(event);
        CurrentUserService current = mock(CurrentUserService.class);
        when(current.get()).thenReturn(user(Role.CLIENT));
        BookingService service = new BookingService(mock(BookingRepository.class), mock(BookingParticipantRepository.class),
                events, current, mock(NotificationService.class), new DtoMapper());

        assertThrows(IllegalStateException.class, () -> service.create(new BookingRequest(1L, 2, null, null)));
    }

    private EventSchedule event(int available) {
        ActivityCategory category = new ActivityCategory();
        category.setId(1L);
        category.setName("Hiking");
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setCategory(category);
        activity.setTitle("Tour");
        activity.setDescription("Desc");
        activity.setPrice(new BigDecimal("100.00"));
        User instructor = user(Role.INSTRUCTOR);
        EventSchedule event = new EventSchedule();
        event.setId(1L);
        event.setActivity(activity);
        event.setInstructor(instructor);
        event.setTotalPlaces(5);
        event.setAvailablePlaces(available);
        return event;
    }

    private User user(Role role) {
        User user = new User();
        user.setId(10L);
        user.setEmail(role.name().toLowerCase() + "@example.com");
        user.setFullName(role.name());
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }
}
