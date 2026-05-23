package com.example.activeleisure.instructor;

import com.example.activeleisure.booking.BookingParticipantRepository;
import com.example.activeleisure.booking.BookingRepository;
import com.example.activeleisure.document.DocumentService;
import com.example.activeleisure.dto.ApiDtos.DocumentResponse;
import com.example.activeleisure.dto.ApiDtos.EventReportRequest;
import com.example.activeleisure.dto.ApiDtos.EventResponse;
import com.example.activeleisure.dto.ApiDtos.ParticipantResponse;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventScheduleRepository;
import com.example.activeleisure.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('INSTRUCTOR')")
@Transactional(readOnly = true)
public class InstructorService {
    private final EventScheduleRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository participantRepository;
    private final CurrentUserService currentUserService;
    private final DocumentService documentService;
    private final DtoMapper mapper;

    public List<EventResponse> events() {
        return eventRepository.findByInstructorId(currentUserService.get().getId()).stream().map(mapper::event).toList();
    }

    public EventResponse event(Long id) {
        return mapper.event(getOwnEvent(id));
    }

    public List<ParticipantResponse> participants(Long id) {
        getOwnEvent(id);
        return bookingRepository.findByEventId(id).stream()
                .flatMap(booking -> participantRepository.findByBookingId(booking.getId()).stream())
                .map(mapper::participant)
                .toList();
    }

    @Transactional
    public DocumentResponse report(Long id, EventReportRequest request) {
        getOwnEvent(id);
        return documentService.eventReport(id);
    }

    private EventSchedule getOwnEvent(Long id) {
        EventSchedule event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));
        if (!event.getInstructor().getId().equals(currentUserService.get().getId())) {
            throw new AccessDeniedException("Мероприятие назначено другому инструктору");
        }
        return event;
    }
}
