package com.example.activeleisure.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentAssignmentRepository extends JpaRepository<EquipmentAssignment, Long> {
    List<EquipmentAssignment> findByEventId(Long eventId);
}
