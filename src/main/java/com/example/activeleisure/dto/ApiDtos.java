package com.example.activeleisure.dto;

import com.example.activeleisure.common.Enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record AuthRequest(
            @Email(message = "Укажите корректный адрес электронной почты")
            @NotBlank(message = "Укажите электронную почту") String email,
            @NotBlank(message = "Укажите пароль") String password) {
    }

    public record RegisterRequest(
            @Email(message = "Укажите корректный адрес электронной почты")
            @NotBlank(message = "Укажите электронную почту") String email,
            @NotBlank(message = "Укажите пароль")
            @Size(min = 6, message = "Пароль должен содержать минимум 6 символов") String password,
            @NotBlank(message = "Укажите ФИО") String fullName, String phone) {
    }

    public record AuthResponse(String accessToken, UserResponse user) {
    }

    public record UserResponse(Long id, String email, String fullName, String phone, Role role,
                               boolean enabled, Instant createdAt, Instant updatedAt) {
    }

    public record UserUpdateRequest(String fullName, String phone, Role role, Boolean enabled) {
    }

    public record EmployeeRequest(@Email(message = "Укажите корректный адрес электронной почты")
                                  @NotBlank(message = "Укажите электронную почту") String email,
                                  String password,
                                  @NotBlank(message = "Укажите ФИО") String fullName, String phone,
                                  @NotNull(message = "Укажите роль") Role role,
                                  String specialization, Integer experienceYears, String bio) {
    }

    public record EmployeeResponse(Long id, UserResponse user, String specialization, Integer experienceYears,
                                   String bio, boolean active) {
    }

    public record CategoryRequest(@NotBlank(message = "Укажите название") String name, String description) {
    }

    public record CategoryResponse(Long id, String name, String description) {
    }

    public record ActivityRequest(@NotBlank(message = "Укажите название") String title,
                                  @NotBlank(message = "Укажите описание") String description, String shortDescription,
                                  @NotNull(message = "Укажите категорию") Long categoryId,
                                  String difficultyLevel,
                                  @Positive(message = "Длительность должна быть больше 0") Integer durationHours,
                                  @NotNull(message = "Укажите цену")
                                  @PositiveOrZero(message = "Цена не может быть отрицательной") BigDecimal price,
                                  String location,
                                  @PositiveOrZero(message = "Возраст не может быть отрицательным") Integer minAge,
                                  String healthRestrictions,
                                  @Positive(message = "Минимальное количество участников должно быть больше 0") Integer minParticipants,
                                  @Positive(message = "Максимальное количество участников должно быть больше 0") Integer maxParticipants,
                                  String requiredEquipmentDescription, String imageUrl, String galleryImages,
                                  String includedServices, String notIncludedServices, String routeDescription,
                                  ActivityStatus status) {
        public ActivityRequest(String title, String description, Long categoryId, String difficultyLevel,
                               Integer durationHours, BigDecimal price, String location, Integer minAge,
                               String healthRestrictions, Integer minParticipants, Integer maxParticipants,
                               String requiredEquipmentDescription, String imageUrl, ActivityStatus status) {
            this(title, description, null, categoryId, difficultyLevel, durationHours, price, location, minAge,
                    healthRestrictions, minParticipants, maxParticipants, requiredEquipmentDescription, imageUrl,
                    null, null, null, null, status);
        }
    }

    public record ActivityResponse(Long id, String title, String description, CategoryResponse category,
                                   String difficultyLevel, Integer durationHours, BigDecimal price, String location,
                                   Integer minAge, String healthRestrictions, Integer minParticipants,
                                   Integer maxParticipants, String requiredEquipmentDescription, String imageUrl,
                                   ActivityStatus status, Instant createdAt, Instant updatedAt,
                                   String shortDescription, String galleryImages, String includedServices,
                                   String notIncludedServices, String routeDescription, Double averageRating,
                                   Long reviewCount, EventSummaryResponse nearestEvent) {
    }

    public record EventSummaryResponse(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       Integer totalPlaces, Integer availablePlaces, EventStatus status) {
    }

    public record ActivityDetailsResponse(ActivityResponse activity, List<EventResponse> nearestEvents,
                                          List<ReviewResponse> reviews, Double averageRating, Long reviewCount) {
    }

    public record EventRequest(@NotNull(message = "Укажите активность") Long activityId,
                               @NotNull(message = "Укажите инструктора") Long instructorId,
                               @NotNull(message = "Укажите дату и время начала") LocalDateTime startDateTime,
                               @NotNull(message = "Укажите дату и время окончания") LocalDateTime endDateTime,
                               @NotNull(message = "Укажите количество мест")
                               @Positive(message = "Количество мест должно быть больше 0") Integer totalPlaces) {
    }

    public record EventUpdateRequest(Long activityId, Long instructorId, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, @Positive Integer totalPlaces, EventStatus status,
                                     String cancellationReason) {
    }

    public record EventResponse(Long id, ActivityResponse activity, UserResponse instructor,
                                LocalDateTime startDateTime, LocalDateTime endDateTime, Integer totalPlaces,
                                Integer availablePlaces, EventStatus status, String cancellationReason,
                                Instant createdAt, Instant updatedAt) {
    }

    public record ParticipantRequest(@NotBlank(message = "Укажите ФИО участника") String fullName,
                                     @Positive(message = "Возраст должен быть больше 0") Integer age, String phone,
                                     String medicalNotes) {
    }

    public record ParticipantResponse(Long id, String fullName, Integer age, String phone, String medicalNotes) {
    }

    public record BookingRequest(@NotNull(message = "Выберите мероприятие") Long eventId,
                                 @NotNull(message = "Укажите количество участников")
                                 @Positive(message = "Количество участников должно быть больше 0") Integer participantsCount,
                                 String comment, @Valid List<ParticipantRequest> participants) {
    }

    public record BookingResponse(Long id, UserResponse client, EventResponse event, Integer participantsCount,
                                  BigDecimal totalPrice, BookingStatus status, String comment,
                                  Instant createdAt, Instant updatedAt, List<ParticipantResponse> participants) {
    }

    public record PaymentCreateRequest(@NotNull(message = "Укажите способ оплаты") PaymentMethod method) {
    }

    public record PaymentResponse(Long id, Long bookingId, BigDecimal amount, PaymentStatus status,
                                  PaymentMethod method, String mockTransactionId, Instant paidAt,
                                  Instant createdAt, Instant updatedAt) {
    }

    public record EquipmentRequest(@NotBlank(message = "Укажите название") String name,
                                   @NotNull(message = "Укажите категорию") Long categoryId,
                                   @NotNull(message = "Укажите общее количество")
                                   @PositiveOrZero(message = "Количество не может быть отрицательным") Integer quantityTotal,
                                   @NotNull(message = "Укажите доступное количество")
                                   @PositiveOrZero(message = "Доступное количество не может быть отрицательным") Integer quantityAvailable,
                                   EquipmentConditionStatus conditionStatus, String description) {
    }

    public record EquipmentResponse(Long id, String name, CategoryResponse category, Integer quantityTotal,
                                    Integer quantityAvailable, EquipmentConditionStatus conditionStatus,
                                    String description, Instant createdAt, Instant updatedAt) {
    }

    public record EquipmentAssignmentRequest(@NotNull(message = "Укажите снаряжение") Long equipmentId,
                                             @NotNull(message = "Укажите количество")
                                             @Positive(message = "Количество должно быть больше 0") Integer quantity) {
    }

    public record EquipmentAssignmentResponse(Long id, EquipmentResponse equipment, Long eventId,
                                              Integer quantity, Instant assignedAt, Instant returnedAt,
                                              EquipmentAssignmentStatus status) {
    }

    public record ReviewRequest(@NotNull(message = "Укажите бронирование") Long bookingId,
                                @Min(value = 1, message = "Оценка должна быть от 1 до 5")
                                @Max(value = 5, message = "Оценка должна быть от 1 до 5") Integer rating,
                                String text) {
    }

    public record ReviewResponse(Long id, UserResponse client, Long activityId, Long bookingId,
                                 Integer rating, String text, boolean moderated, boolean visible,
                                 Instant createdAt) {
    }

    public record NotificationResponse(Long id, String title, String message, NotificationType type,
                                       boolean read, Instant createdAt) {
    }

    public record DocumentResponse(Long id, Long bookingId, DocumentType type, String fileName,
                                   String content, Instant createdAt) {
    }

    public record ReportResponse(String name, Object data) {
    }

    public record ManagerDashboardResponse(long newBookings, long confirmedBookings, long paidBookings,
                                           BigDecimal paidRevenue, List<EventResponse> nearestEvents,
                                           Object popularActivities, Object instructorWorkload) {
    }

    public record AdminDashboardResponse(long users, long clients, long employees, long activities,
                                         long bookings, BigDecimal paidRevenue, List<NotificationResponse> recentActions) {
    }

    public record EventReportRequest(String content) {
    }
}
