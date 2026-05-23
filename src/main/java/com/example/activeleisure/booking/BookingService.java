package com.example.activeleisure.booking;

import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.common.Enums.EventStatus;
import com.example.activeleisure.common.Enums.NotificationType;
import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.BookingRequest;
import com.example.activeleisure.dto.ApiDtos.BookingResponse;
import com.example.activeleisure.dto.ApiDtos.ParticipantRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.notification.NotificationService;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventService;
import com.example.activeleisure.security.CurrentUserService;
import com.example.activeleisure.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository participantRepository;
    private final EventService eventService;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final DtoMapper mapper;

    @Transactional
    @PreAuthorize("hasRole('CLIENT')")
    public BookingResponse create(BookingRequest request) {
        User client = currentUserService.get();
        EventSchedule event = eventService.get(request.eventId());
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя забронировать отменённое или завершённое мероприятие");
        }
        if (event.getAvailablePlaces() < request.participantsCount()) {
            throw new IllegalStateException("Недостаточно свободных мест");
        }
        event.setAvailablePlaces(event.getAvailablePlaces() - request.participantsCount());
        Booking booking = new Booking();
        booking.setClient(client);
        booking.setEvent(event);
        booking.setParticipantsCount(request.participantsCount());
        booking.setTotalPrice(event.getActivity().getPrice().multiply(BigDecimal.valueOf(request.participantsCount())));
        booking.setComment(request.comment());
        Booking saved = bookingRepository.save(booking);
        if (request.participants() != null) {
            for (ParticipantRequest p : request.participants()) {
                BookingParticipant participant = new BookingParticipant();
                participant.setBooking(saved);
                participant.setFullName(p.fullName());
                participant.setAge(p.age());
                participant.setPhone(p.phone());
                participant.setMedicalNotes(p.medicalNotes());
                participantRepository.save(participant);
            }
        }
        notificationService.notify(client, "Бронирование создано", "Бронирование №" + saved.getId() + " создано",
                NotificationType.BOOKING_CREATED);
        return mapper.booking(saved);
    }

    public List<BookingResponse> my() {
        return bookingRepository.findByClientId(currentUserService.get().getId()).stream().map(mapper::booking).toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','INSTRUCTOR')")
    public List<BookingResponse> all() {
        return all(null, null);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','INSTRUCTOR')")
    public List<BookingResponse> all(BookingStatus status, String clientSearch) {
        User current = currentUserService.get();
        List<Booking> bookings;
        if (current.getRole() == Role.INSTRUCTOR) {
            bookings = bookingRepository.findByEventInstructorId(current.getId());
        } else {
            bookings = bookingRepository.findAll();
        }
        return bookings.stream()
                .filter(booking -> status == null || booking.getStatus() == status)
                .filter(booking -> clientSearch == null || clientSearch.isBlank()
                        || booking.getClient().getEmail().toLowerCase().contains(clientSearch.toLowerCase())
                        || booking.getClient().getFullName().toLowerCase().contains(clientSearch.toLowerCase()))
                .map(mapper::booking)
                .toList();
    }

    public BookingResponse one(Long id) {
        Booking booking = get(id);
        assertCanSee(booking);
        return mapper.booking(booking);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public BookingResponse confirm(Long id) {
        Booking booking = get(id);
        booking.setStatus(BookingStatus.CONFIRMED);
        notificationService.notify(booking.getClient(), "Бронирование подтверждено", "Бронирование №" + id + " подтверждено",
                NotificationType.BOOKING_CONFIRMED);
        return mapper.booking(booking);
    }

    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = get(id);
        User current = currentUserService.get();
        if (current.getRole() == Role.CLIENT && !booking.getClient().getId().equals(current.getId())) {
            throw new AccessDeniedException("Бронирование принадлежит другому клиенту");
        }
        cancelInternal(booking);
        notificationService.notify(booking.getClient(), "Бронирование отменено", "Бронирование №" + id + " отменено",
                NotificationType.BOOKING_CANCELLED);
        return mapper.booking(booking);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public BookingResponse complete(Long id) {
        Booking booking = get(id);
        booking.setStatus(BookingStatus.COMPLETED);
        return mapper.booking(booking);
    }

    @Transactional
    public void cancelInternal(Booking booking) {
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            booking.setStatus(BookingStatus.CANCELLED);
            EventSchedule event = booking.getEvent();
            event.setAvailablePlaces(event.getAvailablePlaces() + booking.getParticipantsCount());
        }
    }

    public Booking get(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));
    }

    private void assertCanSee(Booking booking) {
        User current = currentUserService.get();
        if (current.getRole() == Role.ADMIN || current.getRole() == Role.MANAGER) return;
        if (current.getRole() == Role.INSTRUCTOR && booking.getEvent().getInstructor().getId().equals(current.getId())) return;
        if (current.getRole() == Role.CLIENT && booking.getClient().getId().equals(current.getId())) return;
        throw new AccessDeniedException("У вас нет доступа к этому бронированию");
    }
}
