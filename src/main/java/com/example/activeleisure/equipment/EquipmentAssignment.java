package com.example.activeleisure.equipment;

import com.example.activeleisure.common.Enums.EquipmentAssignmentStatus;
import com.example.activeleisure.schedule.EventSchedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "equipment_assignments")
public class EquipmentAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventSchedule event;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false, updatable = false)
    private Instant assignedAt;
    private Instant returnedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentAssignmentStatus status = EquipmentAssignmentStatus.ASSIGNED;

    @PrePersist
    void prePersist() {
        assignedAt = Instant.now();
    }
}
