package com.example.activeleisure.payment;

import com.example.activeleisure.dto.ApiDtos.PaymentCreateRequest;
import com.example.activeleisure.dto.ApiDtos.PaymentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mock Payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/api/bookings/{bookingId}/payments/mock-create")
    public PaymentResponse create(@PathVariable Long bookingId, @Valid @RequestBody PaymentCreateRequest request) {
        return paymentService.createMock(bookingId, request);
    }

    @PatchMapping("/api/payments/{id}/mock-paid")
    public PaymentResponse paid(@PathVariable Long id) {
        return paymentService.paid(id);
    }

    @PatchMapping("/api/payments/{id}/mock-partial")
    public PaymentResponse partial(@PathVariable Long id) {
        return paymentService.partial(id);
    }

    @PatchMapping("/api/payments/{id}/mock-refund")
    public PaymentResponse refund(@PathVariable Long id) {
        return paymentService.refund(id);
    }

    @PatchMapping("/api/payments/{id}/mock-cancel")
    public PaymentResponse cancel(@PathVariable Long id) {
        return paymentService.cancel(id);
    }

    @GetMapping("/api/payments")
    public List<PaymentResponse> all() {
        return paymentService.all();
    }

    @GetMapping("/api/payments/my")
    public List<PaymentResponse> my() {
        return paymentService.my();
    }

    @GetMapping("/api/payments/{id}")
    public PaymentResponse one(@PathVariable Long id) {
        return paymentService.one(id);
    }
}
