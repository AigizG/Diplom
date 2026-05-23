package com.example.activeleisure.config;

import com.example.activeleisure.activity.Activity;
import com.example.activeleisure.activity.ActivityCategory;
import com.example.activeleisure.activity.ActivityCategoryRepository;
import com.example.activeleisure.activity.ActivityRepository;
import com.example.activeleisure.common.Enums.ActivityStatus;
import com.example.activeleisure.common.Enums.EquipmentConditionStatus;
import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.employee.EmployeeProfile;
import com.example.activeleisure.employee.EmployeeProfileRepository;
import com.example.activeleisure.equipment.Equipment;
import com.example.activeleisure.equipment.EquipmentCategory;
import com.example.activeleisure.equipment.EquipmentCategoryRepository;
import com.example.activeleisure.equipment.EquipmentRepository;
import com.example.activeleisure.schedule.EventSchedule;
import com.example.activeleisure.schedule.EventScheduleRepository;
import com.example.activeleisure.user.User;
import com.example.activeleisure.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeRepository;
    private final ActivityCategoryRepository activityCategoryRepository;
    private final ActivityRepository activityRepository;
    private final EventScheduleRepository eventRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        User admin = user("admin@example.com", "admin123", "System Admin", Role.ADMIN);
        user("manager@example.com", "manager123", "Booking Manager", Role.MANAGER);
        User instructor = user("instructor@example.com", "instructor123", "Lead Instructor", Role.INSTRUCTOR);
        user("client@example.com", "client123", "Test Client", Role.CLIENT);
        employee(instructor, "Hiking and rafting", 5);
        employee(admin, "Administration", 10);

        if (activityCategoryRepository.count() == 0) {
            ActivityCategory hiking = category("Hiking", "Mountain and forest routes");
            ActivityCategory rafting = category("Rafting", "Water activities");
            ActivityCategory cycling = category("Cycling", "Bike tours");
            List<Activity> activities = List.of(
                    activity("Mountain hike", hiking, "MEDIUM", "Altai", "One-day mountain route", 5500),
                    activity("Family forest walk", hiking, "EASY", "Moscow region", "Light route for families", 2500),
                    activity("River rafting", rafting, "HARD", "Karelia", "Mock equipment included", 8000),
                    activity("City bike tour", cycling, "EASY", "Saint Petersburg", "Sightseeing by bike", 3000),
                    activity("Weekend bike camp", cycling, "MEDIUM", "Valday", "Two-day active camp", 6500)
            );
            activityRepository.saveAll(activities);
            for (int i = 0; i < 3; i++) {
                EventSchedule event = new EventSchedule();
                event.setActivity(activities.get(i));
                event.setInstructor(instructor);
                event.setStartDateTime(LocalDateTime.now().plusDays(i + 3).withHour(10).withMinute(0));
                event.setEndDateTime(LocalDateTime.now().plusDays(i + 3).withHour(16).withMinute(0));
                event.setTotalPlaces(10);
                event.setAvailablePlaces(10);
                eventRepository.save(event);
            }
        }

        if (equipmentCategoryRepository.count() == 0) {
            EquipmentCategory safety = equipmentCategory("Safety", "Helmets and safety equipment");
            EquipmentCategory transport = equipmentCategory("Transport", "Bikes and boats");
            equipment("Helmet", safety, 20);
            equipment("Mountain bike", transport, 12);
            equipment("Raft boat", transport, 4);
        }
    }

    private User user(String email, String password, String fullName, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setFullName(fullName);
            user.setRole(role);
            user.setEnabled(true);
            return userRepository.save(user);
        });
    }

    private void employee(User user, String specialization, int years) {
        employeeRepository.findByUserId(user.getId()).orElseGet(() -> {
            EmployeeProfile profile = new EmployeeProfile();
            profile.setUser(user);
            profile.setSpecialization(specialization);
            profile.setExperienceYears(years);
            profile.setActive(true);
            return employeeRepository.save(profile);
        });
    }

    private ActivityCategory category(String name, String description) {
        ActivityCategory category = new ActivityCategory();
        category.setName(name);
        category.setDescription(description);
        return activityCategoryRepository.save(category);
    }

    private Activity activity(String title, ActivityCategory category, String difficulty, String location, String description, int price) {
        Activity activity = new Activity();
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setCategory(category);
        activity.setDifficultyLevel(difficulty);
        activity.setDurationHours(6);
        activity.setPrice(BigDecimal.valueOf(price));
        activity.setLocation(location);
        activity.setMinAge(12);
        activity.setMinParticipants(2);
        activity.setMaxParticipants(10);
        activity.setStatus(ActivityStatus.ACTIVE);
        return activity;
    }

    private EquipmentCategory equipmentCategory(String name, String description) {
        EquipmentCategory category = new EquipmentCategory();
        category.setName(name);
        category.setDescription(description);
        return equipmentCategoryRepository.save(category);
    }

    private void equipment(String name, EquipmentCategory category, int quantity) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipment.setCategory(category);
        equipment.setQuantityTotal(quantity);
        equipment.setQuantityAvailable(quantity);
        equipment.setConditionStatus(EquipmentConditionStatus.AVAILABLE);
        equipmentRepository.save(equipment);
    }
}
