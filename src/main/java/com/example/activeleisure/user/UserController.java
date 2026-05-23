package com.example.activeleisure.user;

import com.example.activeleisure.dto.ApiDtos.UserResponse;
import com.example.activeleisure.dto.ApiDtos.UserUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me() {
        return userService.me();
    }

    @GetMapping
    public List<UserResponse> all() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse one(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @PatchMapping("/{id}/block")
    public UserResponse block(@PathVariable Long id) {
        return userService.block(id);
    }

    @PatchMapping("/{id}/unblock")
    public UserResponse unblock(@PathVariable Long id) {
        return userService.unblock(id);
    }
}
