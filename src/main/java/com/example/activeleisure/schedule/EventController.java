package com.example.activeleisure.schedule;

import com.example.activeleisure.dto.ApiDtos.EventRequest;
import com.example.activeleisure.dto.ApiDtos.EventResponse;
import com.example.activeleisure.dto.ApiDtos.EventUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Events")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventResponse> all() {
        return eventService.all();
    }

    @GetMapping("/{id}")
    public EventResponse one(@PathVariable Long id) {
        return eventService.one(id);
    }

    @PostMapping
    public EventResponse create(@Valid @RequestBody EventRequest request) {
        return eventService.create(request);
    }

    @PutMapping("/{id}")
    public EventResponse update(@PathVariable Long id, @RequestBody EventUpdateRequest request) {
        return eventService.update(id, request);
    }

    @PatchMapping("/{id}/cancel")
    public EventResponse cancel(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        return eventService.cancel(id, body == null ? null : body.get("reason"));
    }

    @PatchMapping("/{id}/complete")
    public EventResponse complete(@PathVariable Long id) {
        return eventService.complete(id);
    }

    @PatchMapping("/{id}/postpone")
    public EventResponse postpone(@PathVariable Long id, @RequestBody EventUpdateRequest request) {
        return eventService.postpone(id, request);
    }
}
