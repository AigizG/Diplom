package com.example.activeleisure.booking;

import com.example.activeleisure.dto.ApiDtos.BookingRequest;
import com.example.activeleisure.dto.ApiDtos.BookingResponse;
import com.example.activeleisure.common.Enums.BookingStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bookings")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingRequest request) {
        return bookingService.create(request);
    }

    @GetMapping("/my")
    public List<BookingResponse> my() {
        return bookingService.my();
    }

    @GetMapping
    public List<BookingResponse> all(@RequestParam(required = false) BookingStatus status,
                                     @RequestParam(required = false) String clientSearch) {
        return bookingService.all(status, clientSearch);
    }

    @GetMapping("/{id}")
    public BookingResponse one(@PathVariable Long id) {
        return bookingService.one(id);
    }

    @PatchMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable Long id) {
        return bookingService.confirm(id);
    }

    @PatchMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return bookingService.cancel(id);
    }

    @PatchMapping("/{id}/complete")
    public BookingResponse complete(@PathVariable Long id) {
        return bookingService.complete(id);
    }
}
