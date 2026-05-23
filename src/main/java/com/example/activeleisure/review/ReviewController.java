package com.example.activeleisure.review;

import com.example.activeleisure.dto.ApiDtos.ReviewRequest;
import com.example.activeleisure.dto.ApiDtos.ReviewResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    public ReviewResponse create(@Valid @RequestBody ReviewRequest request) {
        return reviewService.create(request);
    }

    @GetMapping("/api/activities/{activityId}/reviews")
    public List<ReviewResponse> activityReviews(@PathVariable Long activityId) {
        return reviewService.publicByActivity(activityId);
    }

    @GetMapping("/api/reviews")
    public List<ReviewResponse> all() {
        return reviewService.all();
    }

    @PatchMapping("/api/reviews/{id}/moderate")
    public ReviewResponse moderate(@PathVariable Long id) {
        return reviewService.moderate(id);
    }

    @PatchMapping("/api/reviews/{id}/hide")
    public ReviewResponse hide(@PathVariable Long id) {
        return reviewService.hide(id);
    }

    @DeleteMapping("/api/reviews/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}
