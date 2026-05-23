package com.example.activeleisure.review;

import com.example.activeleisure.booking.Booking;
import com.example.activeleisure.booking.BookingService;
import com.example.activeleisure.common.Enums.BookingStatus;
import com.example.activeleisure.dto.ApiDtos.ReviewRequest;
import com.example.activeleisure.dto.ApiDtos.ReviewResponse;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final CurrentUserService currentUserService;
    private final DtoMapper mapper;

    @Transactional
    @PreAuthorize("hasRole('CLIENT')")
    public ReviewResponse create(ReviewRequest request) {
        Booking booking = bookingService.get(request.bookingId());
        if (!booking.getClient().getId().equals(currentUserService.get().getId())) {
            throw new AccessDeniedException("Бронирование принадлежит другому клиенту");
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Отзыв можно оставить только после завершённого бронирования");
        }
        Review review = new Review();
        review.setClient(booking.getClient());
        review.setBooking(booking);
        review.setActivity(booking.getEvent().getActivity());
        review.setRating(request.rating());
        review.setText(request.text());
        return mapper.review(reviewRepository.save(review));
    }

    public List<ReviewResponse> publicByActivity(Long activityId) {
        return reviewRepository.findByActivityIdAndModeratedTrueAndVisibleTrue(activityId).stream().map(mapper::review).toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<ReviewResponse> all() {
        return reviewRepository.findAll().stream().map(mapper::review).toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ReviewResponse moderate(Long id) {
        Review review = get(id);
        review.setModerated(true);
        review.setVisible(true);
        return mapper.review(review);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ReviewResponse hide(Long id) {
        Review review = get(id);
        review.setVisible(false);
        return mapper.review(review);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public void delete(Long id) {
        reviewRepository.delete(get(id));
    }

    private Review get(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Отзыв не найден"));
    }
}
