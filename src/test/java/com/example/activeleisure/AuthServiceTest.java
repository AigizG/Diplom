package com.example.activeleisure;

import com.example.activeleisure.auth.AuthService;
import com.example.activeleisure.common.Enums.Role;
import com.example.activeleisure.dto.ApiDtos.RegisterRequest;
import com.example.activeleisure.mapper.DtoMapper;
import com.example.activeleisure.security.JwtService;
import com.example.activeleisure.user.User;
import com.example.activeleisure.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Test
    void registersClient() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        UserDetailsService detailsService = mock(UserDetailsService.class);
        JwtService jwt = mock(JwtService.class);
        UserDetails details = mock(UserDetails.class);
        when(encoder.encode("client123")).thenReturn("hash");
        when(users.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(detailsService.loadUserByUsername("new@example.com")).thenReturn(details);
        when(jwt.generateToken(details)).thenReturn("token");

        AuthService service = new AuthService(users, encoder, authManager, detailsService, jwt, new DtoMapper());
        var response = service.register(new RegisterRequest("new@example.com", "client123", "New Client", "+1"));

        assertEquals("token", response.accessToken());
        assertEquals(Role.CLIENT, response.user().role());
        verify(users).save(argThat(u -> u.getPasswordHash().equals("hash")));
    }

    @Test
    void rejectsDuplicateEmail() {
        UserRepository users = mock(UserRepository.class);
        when(users.existsByEmail("client@example.com")).thenReturn(true);
        AuthService service = new AuthService(users, mock(PasswordEncoder.class), mock(AuthenticationManager.class),
                mock(UserDetailsService.class), mock(JwtService.class), new DtoMapper());

        assertThrows(IllegalArgumentException.class,
                () -> service.register(new RegisterRequest("client@example.com", "client123", "Client", null)));
    }

    @Test
    void logsInWithValidCredentials() {
        UserRepository users = mock(UserRepository.class);
        User user = new User();
        user.setEmail("client@example.com");
        user.setFullName("Client");
        user.setRole(Role.CLIENT);
        user.setEnabled(true);
        when(users.findByEmail("client@example.com")).thenReturn(Optional.of(user));
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        UserDetailsService detailsService = mock(UserDetailsService.class);
        UserDetails details = mock(UserDetails.class);
        when(detailsService.loadUserByUsername("client@example.com")).thenReturn(details);
        JwtService jwt = mock(JwtService.class);
        when(jwt.generateToken(details)).thenReturn("token");

        AuthService service = new AuthService(users, mock(PasswordEncoder.class), authManager, detailsService, jwt, new DtoMapper());
        var response = service.login(new com.example.activeleisure.dto.ApiDtos.AuthRequest("client@example.com", "client123"));

        assertEquals("token", response.accessToken());
        verify(authManager).authenticate(any());
    }
}
