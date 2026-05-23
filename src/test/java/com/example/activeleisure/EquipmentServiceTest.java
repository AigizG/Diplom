package com.example.activeleisure;

import com.example.activeleisure.common.Enums.EquipmentAssignmentStatus;
import com.example.activeleisure.common.Enums.EquipmentConditionStatus;
import com.example.activeleisure.dto.ApiDtos.EquipmentAssignmentRequest;
import com.example.activeleisure.equipment.*;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EquipmentServiceTest {
    @Test
    void assignsAndReturnsEquipment() {
        Equipment equipment = equipment(5);
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        EventService events = mock(EventService.class);
        EventSchedule event = new EventSchedule();
        event.setId(1L);
        when(events.get(1L)).thenReturn(event);
        EquipmentAssignmentRepository assignments = mock(EquipmentAssignmentRepository.class);
        when(assignments.save(any(EquipmentAssignment.class))).thenAnswer(i -> {
            EquipmentAssignment a = i.getArgument(0);
            a.setId(2L);
            return a;
        });

        EquipmentService service = new EquipmentService(equipmentRepository, mock(EquipmentCategoryRepository.class),
                assignments, events, new DtoMapper());
        var response = service.assign(1L, new EquipmentAssignmentRequest(1L, 2));
        assertEquals(3, equipment.getQuantityAvailable());

        EquipmentAssignment assignment = new EquipmentAssignment();
        assignment.setId(2L);
        assignment.setEquipment(equipment);
        assignment.setEvent(event);
        assignment.setQuantity(2);
        when(assignments.findById(2L)).thenReturn(Optional.of(assignment));
        var returned = service.returnAssignment(2L);
        assertEquals(EquipmentAssignmentStatus.RETURNED, returned.status());
        assertEquals(5, equipment.getQuantityAvailable());
    }

    @Test
    void rejectsOverAssignment() {
        EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment(1)));
        EquipmentService service = new EquipmentService(equipmentRepository, mock(EquipmentCategoryRepository.class),
                mock(EquipmentAssignmentRepository.class), mock(EventService.class), new DtoMapper());

        assertThrows(IllegalStateException.class, () -> service.assign(1L, new EquipmentAssignmentRequest(1L, 2)));
    }

    private Equipment equipment(int available) {
        EquipmentCategory category = new EquipmentCategory();
        category.setId(1L);
        category.setName("Safety");
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Helmet");
        equipment.setCategory(category);
        equipment.setQuantityTotal(5);
        equipment.setQuantityAvailable(available);
        equipment.setConditionStatus(EquipmentConditionStatus.AVAILABLE);
        return equipment;
    }
}
