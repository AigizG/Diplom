package com.example.activeleisure.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByActivityIdAndModeratedTrueAndVisibleTrue(Long activityId);
    Long countByActivityIdAndModeratedTrueAndVisibleTrue(Long activityId);

    @Query("select avg(r.rating) from Review r where r.activity.id = :activityId and r.moderated = true and r.visible = true")
    Double averageVisibleRatingByActivityId(@Param("activityId") Long activityId);
}
