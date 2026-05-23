package com.example.activeleisure.schedule;

import com.example.activeleisure.common.Enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {
    List<EventSchedule> findByInstructorId(Long instructorId);
    List<EventSchedule> findByActivityIdAndStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(Long activityId, EventStatus status, LocalDateTime startDateTime);
    Optional<EventSchedule> findFirstByActivityIdAndStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(Long activityId, EventStatus status, LocalDateTime startDateTime);
    List<EventSchedule> findTop5ByStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(EventStatus status, LocalDateTime startDateTime);

    @Query("""
        select count(e) > 0 from EventSchedule e
        where e.instructor.id = :instructorId
          and e.status <> :cancelled
          and (:ignoreId is null or e.id <> :ignoreId)
          and e.startDateTime < :endDateTime
          and e.endDateTime > :startDateTime
        """)
    boolean instructorBusy(@Param("instructorId") Long instructorId,
                           @Param("startDateTime") LocalDateTime startDateTime,
                           @Param("endDateTime") LocalDateTime endDateTime,
                           @Param("cancelled") EventStatus cancelled,
                           @Param("ignoreId") Long ignoreId);
}
