package com.escuelaing.arsw.pizarraBack.domain.usecases;

import com.escuelaing.arsw.pizarraBack.config.JwtService;
import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
import com.escuelaing.arsw.pizarraBack.infrastructure.controller.LoginRequest;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.UserRepository;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @Mock
    TicketService ticketService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateUser_Success() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user1");
        req.setPassword("pass");

        User user = new User();
        user.setUsername("user1");
        user.setPassword("encodedPass");

        when(repository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token123");

        String token = userService.validateUser(req);

        assertNotNull(token);
        assertEquals("token123", token);
        verify(ticketService).storeTicket("token123", "user1");
    }

    @Test
    void validateUser_UserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setUsername("unknown");
        req.setPassword("pass");

        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.validateUser(req);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void validateUser_WrongPassword() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user1");
        req.setPassword("wrongPass");

        User user = new User();
        user.setUsername("user1");
        user.setPassword("encodedPass");

        when(repository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        String token = userService.validateUser(req);

        assertNull(token);
    }

    @Test
    void registerUser_Success() {
        LoginRequest req = new LoginRequest();
        req.setUsername("newUser");
        req.setPassword("pass");

        when(repository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        User savedUser = new User();
        savedUser.setUsername("newUser");
        savedUser.setPassword("encodedPass");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(req);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
    }

    @Test
    void registerUser_UserExists() {
        LoginRequest req = new LoginRequest();
        req.setUsername("existingUser");
        req.setPassword("pass");

        when(repository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(req);
        });

        assertEquals("Usuario ya existe", ex.getMessage());
    }

    @Test
    void findByName_Success() {
        User user = new User();
        user.setUsername("id123");

        when(repository.findById("id123")).thenReturn(Optional.of(user));

        User found = userService.findByName("id123");

        assertEquals(user, found);
    }

    @Test
    void findByName_NotFound() {
        when(repository.findById("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.findByName("unknown");
        });

        assertEquals("no found", ex.getMessage());
    }
}
