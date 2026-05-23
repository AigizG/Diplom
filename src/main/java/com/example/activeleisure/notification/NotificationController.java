package com.example.activeleisure.notification;

import com.example.activeleisure.dto.ApiDtos.NotificationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notifications")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/my")
    public List<NotificationResponse> my() {
        return notificationService.my();
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse read(@PathVariable Long id) {
        return notificationService.read(id);
    }

    @PatchMapping("/read-all")
    public List<NotificationResponse> readAll() {
        return notificationService.readAll();
    }
}
