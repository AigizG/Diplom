package com.example.activeleisure.employee;

import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.EmployeeRequest;
import com.example.activeleisure.dto.ApiDtos.EmployeeResponse;
import com.example.activeleisure.dto.ApiDtos.EventResponse;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.schedule.EventScheduleRepository;
import com.example.activeleisure.user.User;
import com.example.activeleisure.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeProfileRepository employeeRepository;
    private final UserRepository userRepository;
    private final EventScheduleRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper mapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse create(EmployeeRequest request) {
        if (request.role() == Role.CLIENT) {
            throw new IllegalArgumentException("Для сотрудника нельзя указать роль клиента");
        }
        if (request.password() == null || request.password().length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Пользователь с такой электронной почтой уже зарегистрирован");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setRole(request.role());
        user.setEnabled(true);
        userRepository.save(user);

        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setSpecialization(request.specialization());
        profile.setExperienceYears(request.experienceYears());
        profile.setBio(request.bio());
        return mapper.employee(employeeRepository.save(profile));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<EmployeeResponse> all() {
        return employeeRepository.findAll().stream().map(mapper::employee).toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public EmployeeResponse one(Long id) {
        return mapper.employee(get(id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        EmployeeProfile profile = get(id);
        profile.setSpecialization(request.specialization());
        profile.setExperienceYears(request.experienceYears());
        profile.setBio(request.bio());
        return mapper.employee(profile);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse deactivate(Long id) {
        EmployeeProfile profile = get(id);
        profile.setActive(false);
        profile.getUser().setEnabled(false);
        return mapper.employee(profile);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<EmployeeResponse> instructors() {
        return userRepository.findByRole(Role.INSTRUCTOR).stream()
                .map(u -> employeeRepository.findByUserId(u.getId()).orElse(null))
                .filter(p -> p != null && p.isActive())
                .map(mapper::employee)
                .toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<EventResponse> instructorSchedule(Long instructorId) {
        return eventRepository.findByInstructorId(instructorId).stream().map(mapper::event).toList();
    }

    private EmployeeProfile get(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Сотрудник не найден"));
    }
}
