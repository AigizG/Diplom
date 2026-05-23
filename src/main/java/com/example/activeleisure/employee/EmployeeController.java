package com.example.activeleisure.employee;

import com.example.activeleisure.dto.ApiDtos.EmployeeRequest;
import com.example.activeleisure.dto.ApiDtos.EmployeeResponse;
import com.example.activeleisure.dto.ApiDtos.EventResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Employees")
@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/api/employees")
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping("/api/employees")
    public List<EmployeeResponse> all() {
        return employeeService.all();
    }

    @GetMapping("/api/employees/{id}")
    public EmployeeResponse one(@PathVariable Long id) {
        return employeeService.one(id);
    }

    @PutMapping("/api/employees/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @PatchMapping("/api/employees/{id}/deactivate")
    public EmployeeResponse deactivate(@PathVariable Long id) {
        return employeeService.deactivate(id);
    }

    @GetMapping("/api/instructors")
    public List<EmployeeResponse> instructors() {
        return employeeService.instructors();
    }

    @GetMapping("/api/instructors/{id}/schedule")
    public List<EventResponse> schedule(@PathVariable Long id) {
        return employeeService.instructorSchedule(id);
    }
}
