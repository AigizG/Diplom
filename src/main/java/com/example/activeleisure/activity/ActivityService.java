package com.example.activeleisure.activity;

import com.example.activeleisure.common.Enums.ActivityStatus;
import com.example.activeleisure.dto.ApiDtos.ActivityRequest;
import com.example.activeleisure.dto.ApiDtos.ActivityResponse;
import com.example.activeleisure.dto.ApiDtos.ActivityDetailsResponse;
import com.example.activeleisure.dto.ApiDtos.CategoryRequest;
import com.example.activeleisure.dto.ApiDtos.CategoryResponse;
import com.example.activeleisure.common.Enums.EventStatus;
import com.example.activeleisure.review.ReviewRepository;
import com.example.activeleisure.schedule.EventScheduleRepository;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.schedule.EventSchedule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityCategoryRepository categoryRepository;
    private final EventScheduleRepository eventRepository;
    private final ReviewRepository reviewRepository;
    private final DtoMapper mapper;

    public ActivityService(ActivityRepository activityRepository, ActivityCategoryRepository categoryRepository,
                           DtoMapper mapper) {
        this(activityRepository, categoryRepository, null, null, mapper);
    }

    @Autowired
    public ActivityService(ActivityRepository activityRepository, ActivityCategoryRepository categoryRepository,
                           EventScheduleRepository eventRepository, ReviewRepository reviewRepository,
                           DtoMapper mapper) {
        this.activityRepository = activityRepository;
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.reviewRepository = reviewRepository;
        this.mapper = mapper;
    }

    public List<ActivityResponse> find(String category, String difficultyLevel, String location,
                                       String search, BigDecimal minPrice, BigDecimal maxPrice,
                                       Integer minDurationHours, Integer maxDurationHours,
                                       ActivityStatus status, LocalDate date, String sort) {
        Specification<Activity> spec = Specification.where(null);
        if (category != null) spec = spec.and((root, q, cb) -> {
            try {
                return cb.equal(root.join("category").get("id"), Long.parseLong(category));
            } catch (NumberFormatException ex) {
                return cb.equal(root.join("category").get("name"), category);
            }
        });
        if (difficultyLevel != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("difficultyLevel"), difficultyLevel));
        if (location != null) spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
        if (search != null) spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"));
        if (minPrice != null) spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        if (maxPrice != null) spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        if (minDurationHours != null) spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("durationHours"), minDurationHours));
        if (maxDurationHours != null) spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("durationHours"), maxDurationHours));
        if (status != null) spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            spec = spec.and((root, q, cb) -> {
                var sub = q.subquery(Long.class);
                var event = sub.from(EventSchedule.class);
                sub.select(event.get("id"))
                        .where(
                                cb.equal(event.get("activity"), root),
                                cb.greaterThanOrEqualTo(event.get("startDateTime"), start),
                                cb.lessThan(event.get("startDateTime"), end)
                        );
                return cb.exists(sub);
            });
        }
        return activityRepository.findAll(spec, parseSort(sort)).stream().map(mapper::activity).toList();
    }

    public ActivityResponse one(Long id) {
        return mapper.activity(get(id));
    }

    public ActivityDetailsResponse details(Long id) {
        Activity activity = get(id);
        if (eventRepository == null || reviewRepository == null) {
            return new ActivityDetailsResponse(mapper.activity(activity), List.of(), List.of(), null, 0L);
        }
        var events = eventRepository.findByActivityIdAndStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(
                id, EventStatus.PLANNED, LocalDateTime.now()).stream().map(mapper::event).toList();
        var reviews = reviewRepository.findByActivityIdAndModeratedTrueAndVisibleTrue(id).stream().map(mapper::review).toList();
        Double average = reviewRepository.averageVisibleRatingByActivityId(id);
        Long count = reviewRepository.countByActivityIdAndModeratedTrueAndVisibleTrue(id);
        return new ActivityDetailsResponse(mapper.activity(activity), events, reviews, average, count);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ActivityResponse create(ActivityRequest request) {
        Activity activity = new Activity();
        apply(activity, request);
        return mapper.activity(activityRepository.save(activity));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ActivityResponse update(Long id, ActivityRequest request) {
        Activity activity = get(id);
        apply(activity, request);
        return mapper.activity(activity);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public void delete(Long id) {
        activityRepository.delete(get(id));
    }

    public List<CategoryResponse> categories() {
        return categoryRepository.findAll().stream().map(mapper::category).toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request) {
        ActivityCategory category = new ActivityCategory();
        category.setName(request.name());
        category.setDescription(request.description());
        return mapper.category(categoryRepository.save(category));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        ActivityCategory category = getCategory(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return mapper.category(category);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public void deleteCategory(Long id) {
        categoryRepository.delete(getCategory(id));
    }

    private void apply(Activity activity, ActivityRequest request) {
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setShortDescription(request.shortDescription());
        activity.setCategory(getCategory(request.categoryId()));
        activity.setDifficultyLevel(request.difficultyLevel());
        activity.setDurationHours(request.durationHours());
        activity.setPrice(request.price());
        activity.setLocation(request.location());
        activity.setMinAge(request.minAge());
        activity.setHealthRestrictions(request.healthRestrictions());
        activity.setMinParticipants(request.minParticipants());
        activity.setMaxParticipants(request.maxParticipants());
        activity.setRequiredEquipmentDescription(request.requiredEquipmentDescription());
        activity.setImageUrl(request.imageUrl());
        activity.setGalleryImages(request.galleryImages());
        activity.setIncludedServices(request.includedServices());
        activity.setNotIncludedServices(request.notIncludedServices());
        activity.setRouteDescription(request.routeDescription());
        activity.setStatus(request.status() == null ? ActivityStatus.ACTIVE : request.status());
    }

    private Sort parseSort(String sort) {
        String[] parts = sort == null ? new String[]{"title", "asc"} : sort.split(",");
        String field = switch (parts[0]) {
            case "price", "durationHours", "createdAt", "title" -> parts[0];
            default -> "title";
        };
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    public Activity get(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Активность не найдена"));
    }

    public ActivityCategory getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Категория активности не найдена"));
    }
}
