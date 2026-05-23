package com.example.activeleisure.document;

import com.example.activeleisure.dto.ApiDtos.DocumentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Documents")
@RestController
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("/api/bookings/{bookingId}/documents/contract")
    public DocumentResponse contract(@PathVariable Long bookingId) {
        return documentService.contract(bookingId);
    }

    @PostMapping("/api/events/{eventId}/documents/participant-list")
    public DocumentResponse participantList(@PathVariable Long eventId) {
        return documentService.participantList(eventId);
    }

    @PostMapping("/api/events/{eventId}/documents/event-report")
    public DocumentResponse eventReport(@PathVariable Long eventId) {
        return documentService.eventReport(eventId);
    }

    @GetMapping("/api/documents/{id}")
    public DocumentResponse one(@PathVariable Long id) {
        return documentService.one(id);
    }

    @GetMapping("/api/bookings/{bookingId}/documents")
    public List<DocumentResponse> byBooking(@PathVariable Long bookingId) {
        return documentService.byBooking(bookingId);
    }
}
