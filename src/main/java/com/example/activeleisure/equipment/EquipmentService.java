package com.example.activeleisure.equipment;

import com.example.activeleisure.common.Enums.EquipmentAssignmentStatus;
import com.example.activeleisure.common.Enums.EquipmentConditionStatus;
import com.example.activeleisure.dto.ApiDtos.*;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.schedule.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@Transactional(readOnly = true)
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentCategoryRepository categoryRepository;
    private final EquipmentAssignmentRepository assignmentRepository;
    private final EventService eventService;
    private final DtoMapper mapper;

    public List<EquipmentResponse> all() {
        return equipmentRepository.findAll().stream().map(mapper::equipment).toList();
    }

    public EquipmentResponse one(Long id) {
        return mapper.equipment(get(id));
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest request) {
        Equipment e = new Equipment();
        apply(e, request);
        return mapper.equipment(equipmentRepository.save(e));
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment e = get(id);
        apply(e, request);
        return mapper.equipment(e);
    }

    @Transactional
    public void delete(Long id) {
        equipmentRepository.delete(get(id));
    }

    @Transactional
    public EquipmentAssignmentResponse assign(Long eventId, EquipmentAssignmentRequest request) {
        Equipment equipment = get(request.equipmentId());
        if (equipment.getConditionStatus() == EquipmentConditionStatus.WRITTEN_OFF) {
            throw new IllegalStateException("Нельзя назначить списанное снаряжение");
        }
        if (equipment.getQuantityAvailable() < request.quantity()) {
            throw new IllegalStateException("Снаряжение недоступно в нужном количестве");
        }
        equipment.setQuantityAvailable(equipment.getQuantityAvailable() - request.quantity());
        EquipmentAssignment assignment = new EquipmentAssignment();
        assignment.setEquipment(equipment);
        assignment.setEvent(eventService.get(eventId));
        assignment.setQuantity(request.quantity());
        return mapper.assignment(assignmentRepository.save(assignment));
    }

    @Transactional
    public EquipmentAssignmentResponse returnAssignment(Long id) {
        EquipmentAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Назначение снаряжения не найдено"));
        if (assignment.getStatus() != EquipmentAssignmentStatus.RETURNED) {
            assignment.setStatus(EquipmentAssignmentStatus.RETURNED);
            assignment.setReturnedAt(Instant.now());
            Equipment equipment = assignment.getEquipment();
            equipment.setQuantityAvailable(equipment.getQuantityAvailable() + assignment.getQuantity());
        }
        return mapper.assignment(assignment);
    }

    public List<EquipmentAssignmentResponse> eventEquipment(Long eventId) {
        return assignmentRepository.findByEventId(eventId).stream().map(mapper::assignment).toList();
    }

    public List<CategoryResponse> categories() {
        return categoryRepository.findAll().stream().map(mapper::category).toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        EquipmentCategory category = new EquipmentCategory();
        category.setName(request.name());
        category.setDescription(request.description());
        return mapper.category(categoryRepository.save(category));
    }

    private void apply(Equipment e, EquipmentRequest request) {
        e.setName(request.name());
        e.setCategory(categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категория снаряжения не найдена")));
        e.setQuantityTotal(request.quantityTotal());
        e.setQuantityAvailable(request.quantityAvailable());
        e.setConditionStatus(request.conditionStatus() == null ? EquipmentConditionStatus.AVAILABLE : request.conditionStatus());
        e.setDescription(request.description());
    }

    public Equipment get(Long id) {
        return equipmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Снаряжение не найдено"));
    }
}
