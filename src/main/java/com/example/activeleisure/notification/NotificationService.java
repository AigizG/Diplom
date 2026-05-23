package com.example.activeleisure.notification;

import com.example.activeleisure.common.Enums.NotificationType;
import com.example.activeleisure.dto.ApiDtos.NotificationResponse;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.security.CurrentUserService;
import com.example.activeleisure.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;
    private final DtoMapper mapper;

    @Transactional
    public void notify(User user, String title, String message, NotificationType type) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        notificationRepository.save(n);
    }

    public List<NotificationResponse> my() {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUserService.get().getId())
                .stream().map(mapper::notification).toList();
    }

    @Transactional
    public NotificationResponse read(Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Уведомление не найдено"));
        if (!n.getUser().getId().equals(currentUserService.get().getId())) {
            throw new AccessDeniedException("Уведомление принадлежит другому пользователю");
        }
        n.setRead(true);
        return mapper.notification(n);
    }

    @Transactional
    public List<NotificationResponse> readAll() {
        User user = currentUserService.get();
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        list.forEach(n -> n.setRead(true));
        return list.stream().map(mapper::notification).toList();
    }
}
