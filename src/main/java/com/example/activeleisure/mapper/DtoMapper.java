package com.example.activeleisure.mapper;

import com.example.activeleisure.activity.Activity;
import com.example.activeleisure.activity.ActivityCategory;
import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.booking.BookingParticipant;
import com.example.activeleisure.booking.BookingParticipantRepository;
import com.example.activeleisure.document.Document;
import com.example.activeleisure.dto.ApiDtos.*;
import com.example.activeleisure.employee.EmployeeProfile;
import com.example.activeleisure.equipment.Equipment;
import com.example.activeleisure.equipment.EquipmentAssignment;
import com.example.activeleisure.equipment.EquipmentCategory;
import com.example.activeleisure.notification.Notification;
import com.example.activeleisure.payment.Payment;
import com.example.activeleisure.review.ReviewRepository;
import com.example.activeleisure.review.Review;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventScheduleRepository;
import com.example.activeleisure.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    private BookingParticipantRepository participantRepository;
    private ReviewRepository reviewRepository;
    private EventScheduleRepository eventRepository;

    public DtoMapper() {
    }

    @Autowired
    public DtoMapper(BookingParticipantRepository participantRepository,
                     ReviewRepository reviewRepository,
                     EventScheduleRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
    }

    public UserResponse user(User u) {
        if (u == null) return null;
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getRole(),
                u.isEnabled(), u.getCreatedAt(), u.getUpdatedAt());
    }

    public CategoryResponse category(ActivityCategory c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    public CategoryResponse category(EquipmentCategory c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    public ActivityResponse activity(Activity a) {
        Double averageRating = reviewRepository == null ? null : reviewRepository.averageVisibleRatingByActivityId(a.getId());
        Long reviewCount = reviewRepository == null ? 0L : reviewRepository.countByActivityIdAndModeratedTrueAndVisibleTrue(a.getId());
        EventSummaryResponse nearestEvent = eventRepository == null ? null : eventRepository.findFirstByActivityIdAndStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(
                a.getId(), com.example.activeleisure.common.Enums.EventStatus.PLANNED, java.time.LocalDateTime.now()).map(this::eventSummary).orElse(null);
        return new ActivityResponse(a.getId(), a.getTitle(), a.getDescription(), category(a.getCategory()),
                a.getDifficultyLevel(), a.getDurationHours(), a.getPrice(), a.getLocation(), a.getMinAge(),
                a.getHealthRestrictions(), a.getMinParticipants(), a.getMaxParticipants(),
                a.getRequiredEquipmentDescription(), a.getImageUrl(), a.getStatus(), a.getCreatedAt(), a.getUpdatedAt(),
                a.getShortDescription(), a.getGalleryImages(), a.getIncludedServices(), a.getNotIncludedServices(),
                a.getRouteDescription(), averageRating, reviewCount, nearestEvent);
    }

    public EventSummaryResponse eventSummary(EventSchedule e) {
        return new EventSummaryResponse(e.getId(), e.getStartDateTime(), e.getEndDateTime(), e.getTotalPlaces(),
                e.getAvailablePlaces(), e.getStatus());
    }

    public EventResponse event(EventSchedule e) {
        return new EventResponse(e.getId(), activity(e.getActivity()), user(e.getInstructor()), e.getStartDateTime(),
                e.getEndDateTime(), e.getTotalPlaces(), e.getAvailablePlaces(), e.getStatus(),
                e.getCancellationReason(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public BookingResponse booking(Booking b) {
        java.util.List<ParticipantResponse> participants = participantRepository == null ? java.util.List.of() : participantRepository.findByBookingId(b.getId())
                .stream().map(this::participant).toList();
        return new BookingResponse(b.getId(), user(b.getClient()), event(b.getEvent()), b.getParticipantsCount(),
                b.getTotalPrice(), b.getStatus(), b.getComment(), b.getCreatedAt(), b.getUpdatedAt(), participants);
    }

    public ParticipantResponse participant(BookingParticipant p) {
        return new ParticipantResponse(p.getId(), p.getFullName(), p.getAge(), p.getPhone(), p.getMedicalNotes());
    }

    public PaymentResponse payment(Payment p) {
        return new PaymentResponse(p.getId(), p.getBooking().getId(), p.getAmount(), p.getStatus(), p.getMethod(),
                p.getMockTransactionId(), p.getPaidAt(), p.getCreatedAt(), p.getUpdatedAt());
    }

    public EmployeeResponse employee(EmployeeProfile p) {
        return new EmployeeResponse(p.getId(), user(p.getUser()), p.getSpecialization(), p.getExperienceYears(),
                p.getBio(), p.isActive());
    }

    public EquipmentResponse equipment(Equipment e) {
        return new EquipmentResponse(e.getId(), e.getName(), category(e.getCategory()), e.getQuantityTotal(),
                e.getQuantityAvailable(), e.getConditionStatus(), e.getDescription(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public EquipmentAssignmentResponse assignment(EquipmentAssignment a) {
        return new EquipmentAssignmentResponse(a.getId(), equipment(a.getEquipment()), a.getEvent().getId(),
                a.getQuantity(), a.getAssignedAt(), a.getReturnedAt(), a.getStatus());
    }

    public ReviewResponse review(Review r) {
        return new ReviewResponse(r.getId(), user(r.getClient()), r.getActivity().getId(), r.getBooking().getId(),
                r.getRating(), r.getText(), r.isModerated(), r.isVisible(), r.getCreatedAt());
    }

    public NotificationResponse notification(Notification n) {
        return new NotificationResponse(n.getId(), n.getTitle(), n.getMessage(), n.getType(), n.isRead(), n.getCreatedAt());
    }

    public DocumentResponse document(Document d) {
        Long bookingId = d.getBooking() == null ? null : d.getBooking().getId();
        return new DocumentResponse(d.getId(), bookingId, d.getType(), d.getFileName(), d.getContent(), d.getCreatedAt());
    }
}
