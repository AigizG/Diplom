package com.example.activeleisure.instructor;

import com.example.activeleisure.dto.ApiDtos.DocumentResponse;
import com.example.activeleisure.dto.ApiDtos.EventReportRequest;
import com.example.activeleisure.dto.ApiDtos.EventResponse;
import com.example.activeleisure.dto.ApiDtos.ParticipantResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Instructor")
@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;

    @GetMapping("/events")
    public List<EventResponse> events() {
        return instructorService.events();
    }

    @GetMapping("/events/{id}")
    public EventResponse event(@PathVariable Long id) {
        return instructorService.event(id);
    }

    @GetMapping("/events/{id}/participants")
    public List<ParticipantResponse> participants(@PathVariable Long id) {
        return instructorService.participants(id);
    }

    @PostMapping("/events/{id}/report")
    public DocumentResponse report(@PathVariable Long id, @RequestBody(required = false) EventReportRequest request) {
        return instructorService.report(id, request);
    }
}
