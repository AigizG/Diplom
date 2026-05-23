package com.example.activeleisure.equipment;

import com.example.activeleisure.dto.ApiDtos.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Equipment")
@RestController
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @GetMapping("/api/equipment")
    public List<EquipmentResponse> all() {
        return equipmentService.all();
    }

    @PostMapping("/api/equipment")
    public EquipmentResponse create(@Valid @RequestBody EquipmentRequest request) {
        return equipmentService.create(request);
    }

    @GetMapping("/api/equipment/{id}")
    public EquipmentResponse one(@PathVariable Long id) {
        return equipmentService.one(id);
    }

    @PutMapping("/api/equipment/{id}")
    public EquipmentResponse update(@PathVariable Long id, @Valid @RequestBody EquipmentRequest request) {
        return equipmentService.update(id, request);
    }

    @DeleteMapping("/api/equipment/{id}")
    public void delete(@PathVariable Long id) {
        equipmentService.delete(id);
    }

    @PostMapping("/api/events/{eventId}/equipment")
    public EquipmentAssignmentResponse assign(@PathVariable Long eventId, @Valid @RequestBody EquipmentAssignmentRequest request) {
        return equipmentService.assign(eventId, request);
    }

    @PatchMapping("/api/equipment-assignments/{id}/return")
    public EquipmentAssignmentResponse returnAssignment(@PathVariable Long id) {
        return equipmentService.returnAssignment(id);
    }

    @GetMapping("/api/events/{eventId}/equipment")
    public List<EquipmentAssignmentResponse> eventEquipment(@PathVariable Long eventId) {
        return equipmentService.eventEquipment(eventId);
    }

    @GetMapping("/api/equipment-categories")
    public List<CategoryResponse> categories() {
        return equipmentService.categories();
    }

    @PostMapping("/api/equipment-categories")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return equipmentService.createCategory(request);
    }
}
