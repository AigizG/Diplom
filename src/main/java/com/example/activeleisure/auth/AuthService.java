package com.example.activeleisure.auth;

import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.AuthRequest;
import com.example.activeleisure.dto.ApiDtos.AuthResponse;
import com.example.activeleisure.dto.ApiDtos.RegisterRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.security.JwtService;
import com.example.activeleisure.user.User;
import com.example.activeleisure.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final DtoMapper mapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Пользователь с такой электронной почтой уже зарегистрирован");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setRole(Role.CLIENT);
        user.setEnabled(true);
        userRepository.save(user);
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return new AuthResponse(token, mapper.user(user));
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return new AuthResponse(token, mapper.user(user));
    }
}
