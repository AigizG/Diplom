package com.example.activeleisure.activity;

import com.example.activeleisure.common.Enums.ActivityStatus;
import com.example.activeleisure.dto.ApiDtos.ActivityRequest;
import com.example.activeleisure.dto.ApiDtos.ActivityResponse;
import com.example.activeleisure.dto.ApiDtos.ActivityDetailsResponse;
import com.example.activeleisure.dto.ApiDtos.CategoryRequest;
import com.example.activeleisure.dto.ApiDtos.CategoryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Activities")
@RestController
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/api/activities")
    public List<ActivityResponse> all(@RequestParam(required = false) String category,
                                      @RequestParam(required = false) String difficultyLevel,
                                      @RequestParam(required = false) String location,
                                      @RequestParam(required = false) String search,
                                      @RequestParam(required = false) BigDecimal minPrice,
                                      @RequestParam(required = false) BigDecimal maxPrice,
                                      @RequestParam(required = false) Integer minDurationHours,
                                      @RequestParam(required = false) Integer maxDurationHours,
                                      @RequestParam(required = false) ActivityStatus status,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam(defaultValue = "title,asc") String sort) {
        return activityService.find(category, difficultyLevel, location, search, minPrice, maxPrice,
                minDurationHours, maxDurationHours, status, date, sort);
    }

    @GetMapping("/api/activities/{id}")
    public ActivityResponse one(@PathVariable Long id) {
        return activityService.one(id);
    }

    @GetMapping("/api/activities/{id}/details")
    public ActivityDetailsResponse details(@PathVariable Long id) {
        return activityService.details(id);
    }

    @PostMapping("/api/activities")
    public ActivityResponse create(@Valid @RequestBody ActivityRequest request) {
        return activityService.create(request);
    }

    @PutMapping("/api/activities/{id}")
    public ActivityResponse update(@PathVariable Long id, @Valid @RequestBody ActivityRequest request) {
        return activityService.update(id, request);
    }

    @DeleteMapping("/api/activities/{id}")
    public void delete(@PathVariable Long id) {
        activityService.delete(id);
    }

    @GetMapping("/api/activity-categories")
    public List<CategoryResponse> categories() {
        return activityService.categories();
    }

    @PostMapping("/api/activity-categories")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return activityService.createCategory(request);
    }

    @PutMapping("/api/activity-categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return activityService.updateCategory(id, request);
    }

    @DeleteMapping("/api/activity-categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        activityService.deleteCategory(id);
    }
}
