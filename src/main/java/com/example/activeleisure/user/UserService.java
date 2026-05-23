package com.example.activeleisure.user;

import com.example.activeleisure.dto.ApiDtos.UserResponse;
import com.example.activeleisure.dto.ApiDtos.UserUpdateRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final DtoMapper mapper;

    public UserResponse me() {
        return mapper.user(currentUserService.get());
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(mapper::user).toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public UserResponse findById(Long id) {
        return mapper.user(get(id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = get(id);
        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.role() != null) user.setRole(request.role());
        if (request.enabled() != null) user.setEnabled(request.enabled());
        return mapper.user(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse block(Long id) {
        User user = get(id);
        user.setEnabled(false);
        return mapper.user(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse unblock(Long id) {
        User user = get(id);
        user.setEnabled(true);
        return mapper.user(user);
    }

    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}
