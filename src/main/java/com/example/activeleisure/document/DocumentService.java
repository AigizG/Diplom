package com.example.activeleisure.document;

import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.booking.BookingService;
import com.example.activeleisure.common.Enums.DocumentType;
import com.example.activeleisure.dto.ApiDtos.DocumentResponse;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN','INSTRUCTOR')")
@Transactional(readOnly = true)
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final BookingService bookingService;
    private final EventService eventService;
    private final DtoMapper mapper;

    @Transactional
    public DocumentResponse contract(Long bookingId) {
        Booking booking = bookingService.get(bookingId);
        return mapper.document(save(booking, DocumentType.CONTRACT, "contract-" + bookingId + ".html",
                "<h1>Договор</h1><p>Бронирование №" + bookingId + ", сумма " + booking.getTotalPrice() + "</p>"));
    }

    @Transactional
    public DocumentResponse participantList(Long eventId) {
        EventSchedule event = eventService.get(eventId);
        String content = "<h1>Список участников мероприятия №" + eventId + "</h1><p>Активность: "
                + event.getActivity().getTitle() + "</p>";
        return mapper.document(save(null, DocumentType.PARTICIPANT_LIST, "participants-event-" + eventId + ".html", content));
    }

    @Transactional
    public DocumentResponse eventReport(Long eventId) {
        EventSchedule event = eventService.get(eventId);
        String content = "<h1>Отчёт по мероприятию №" + eventId + "</h1><p>Статус: " + event.getStatus() + "</p>";
        return mapper.document(save(null, DocumentType.EVENT_REPORT, "event-report-" + eventId + ".html", content));
    }

    public DocumentResponse one(Long id) {
        return mapper.document(documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Документ не найден")));
    }

    public List<DocumentResponse> byBooking(Long bookingId) {
        return documentRepository.findByBookingId(bookingId).stream().map(mapper::document).toList();
    }

    private Document save(Booking booking, DocumentType type, String fileName, String content) {
        Document document = new Document();
        document.setBooking(booking);
        document.setType(type);
        document.setFileName(fileName);
        document.setContent(content);
        return documentRepository.save(document);
    }
}
