package com.example.activeleisure;

import com.example.activeleisure.activity.*;
import com.example.activeleisure.common.Enums.ActivityStatus;
import com.example.activeleisure.dto.ApiDtos.ActivityRequest;
import com.example.activeleisure.mapper.DtoMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivityServiceTest {
    @Test
    void createsActivity() {
        ActivityCategory category = new ActivityCategory();
        category.setId(1L);
        category.setName("Hiking");
        ActivityCategoryRepository categories = mock(ActivityCategoryRepository.class);
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        ActivityRepository activities = mock(ActivityRepository.class);
        when(activities.save(any(Activity.class))).thenAnswer(i -> {
            Activity a = i.getArgument(0);
            a.setId(2L);
            return a;
        });
        ActivityService service = new ActivityService(activities, categories, new DtoMapper());

        var response = service.create(new ActivityRequest("Tour", "Desc", 1L, "EASY", 3,
                new BigDecimal("1000.00"), "Park", 10, null, 1, 10, null, null, ActivityStatus.ACTIVE));

        assertEquals("Tour", response.title());
        assertEquals("Hiking", response.category().name());
    }
}
