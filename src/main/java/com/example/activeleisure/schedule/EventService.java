package com.example.activeleisure.schedule;

import com.example.activeleisure.activity.ActivityService;
import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.booking.BookingRepository;
import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.common.Enums.EventStatus;
import com.example.activeleisure.common.Enums.NotificationType;
import com.example.activeleisure.dto.ApiDtos.EventRequest;
import com.example.activeleisure.dto.ApiDtos.EventResponse;
import com.example.activeleisure.dto.ApiDtos.EventUpdateRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.notification.NotificationService;
import com.example.activeleisure.user.User;
import com.example.activeleisure.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventScheduleRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final ActivityService activityService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final DtoMapper mapper;

    public List<EventResponse> all() {
        return eventRepository.findAll().stream().map(mapper::event).toList();
    }

    public EventResponse one(Long id) {
        return mapper.event(get(id));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public EventResponse create(EventRequest request) {
        if (!request.endDateTime().isAfter(request.startDateTime())) {
            throw new IllegalArgumentException("Дата окончания мероприятия должна быть позже даты начала");
        }
        User instructor = userService.get(request.instructorId());
        if (instructor.getRole() != com.example.activeleisure.common.Enums.Role.INSTRUCTOR) {
            throw new IllegalArgumentException("Назначенный пользователь должен быть инструктором");
        }
        if (eventRepository.instructorBusy(instructor.getId(), request.startDateTime(), request.endDateTime(),
                EventStatus.CANCELLED, null)) {
            throw new IllegalStateException("Нельзя назначить инструктора на пересекающиеся мероприятия");
        }
        EventSchedule event = new EventSchedule();
        event.setActivity(activityService.get(request.activityId()));
        event.setInstructor(instructor);
        event.setStartDateTime(request.startDateTime());
        event.setEndDateTime(request.endDateTime());
        event.setTotalPlaces(request.totalPlaces());
        event.setAvailablePlaces(request.totalPlaces());
        EventSchedule saved = eventRepository.save(event);
        notificationService.notify(instructor, "Инструктор назначен",
                "Вы назначены на мероприятие №" + saved.getId(), NotificationType.INSTRUCTOR_ASSIGNED);
        return mapper.event(saved);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public EventResponse update(Long id, EventUpdateRequest request) {
        EventSchedule event = get(id);
        if (request.activityId() != null) event.setActivity(activityService.get(request.activityId()));
        if (request.instructorId() != null) event.setInstructor(userService.get(request.instructorId()));
        if (request.startDateTime() != null) event.setStartDateTime(request.startDateTime());
        if (request.endDateTime() != null) event.setEndDateTime(request.endDateTime());
        if (!event.getEndDateTime().isAfter(event.getStartDateTime())) {
            throw new IllegalArgumentException("Дата окончания мероприятия должна быть позже даты начала");
        }
        if (eventRepository.instructorBusy(event.getInstructor().getId(), event.getStartDateTime(), event.getEndDateTime(),
                EventStatus.CANCELLED, event.getId())) {
            throw new IllegalStateException("Нельзя назначить инструктора на пересекающиеся мероприятия");
        }
        if (request.totalPlaces() != null) {
            int booked = event.getTotalPlaces() - event.getAvailablePlaces();
            if (request.totalPlaces() < booked) throw new IllegalStateException("Количество мест меньше уже оформленных бронирований");
            event.setTotalPlaces(request.totalPlaces());
            event.setAvailablePlaces(request.totalPlaces() - booked);
        }
        if (request.status() != null) event.setStatus(request.status());
        event.setCancellationReason(request.cancellationReason());
        notificationService.notify(event.getInstructor(), "Мероприятие изменено", "Мероприятие №" + id + " изменено", NotificationType.EVENT_CHANGED);
        return mapper.event(event);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public EventResponse cancel(Long id, String reason) {
        EventSchedule event = get(id);
        event.setStatus(EventStatus.CANCELLED);
        event.setCancellationReason(reason);
        notificationService.notify(event.getInstructor(), "Мероприятие отменено", "Мероприятие №" + id + " отменено", NotificationType.EVENT_CHANGED);
        for (Booking booking : bookingRepository.findByEventId(id)) {
            if (booking.getStatus() != BookingStatus.CANCELLED && booking.getStatus() != BookingStatus.COMPLETED) {
                booking.setStatus(BookingStatus.CANCELLED);
                event.setAvailablePlaces(event.getAvailablePlaces() + booking.getParticipantsCount());
                notificationService.notify(booking.getClient(), "Мероприятие отменено",
                        "Мероприятие №" + id + " отменено", NotificationType.EVENT_CHANGED);
            }
        }
        return mapper.event(event);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public EventResponse complete(Long id) {
        EventSchedule event = get(id);
        event.setStatus(EventStatus.COMPLETED);
        for (Booking booking : bookingRepository.findByEventId(id)) {
            if (booking.getStatus() != BookingStatus.CANCELLED) booking.setStatus(BookingStatus.COMPLETED);
        }
        return mapper.event(event);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public EventResponse postpone(Long id, EventUpdateRequest request) {
        EventSchedule event = get(id);
        event.setStartDateTime(request.startDateTime());
        event.setEndDateTime(request.endDateTime());
        if (!event.getEndDateTime().isAfter(event.getStartDateTime())) {
            throw new IllegalArgumentException("Дата окончания мероприятия должна быть позже даты начала");
        }
        if (eventRepository.instructorBusy(event.getInstructor().getId(), event.getStartDateTime(), event.getEndDateTime(),
                EventStatus.CANCELLED, event.getId())) {
            throw new IllegalStateException("Нельзя назначить инструктора на пересекающиеся мероприятия");
        }
        event.setStatus(EventStatus.POSTPONED);
        notificationService.notify(event.getInstructor(), "Мероприятие перенесено", "Мероприятие №" + id + " перенесено", NotificationType.EVENT_CHANGED);
        for (Booking booking : bookingRepository.findByEventId(id)) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                notificationService.notify(booking.getClient(), "Мероприятие перенесено",
                        "Мероприятие №" + id + " перенесено", NotificationType.EVENT_CHANGED);
            }
        }
        return mapper.event(event);
    }

    public EventSchedule get(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));
    }
}
