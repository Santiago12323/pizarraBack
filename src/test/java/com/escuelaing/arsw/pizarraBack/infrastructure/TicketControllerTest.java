package com.escuelaing.arsw.pizarraBack.infrastructure.controller;

import com.escuelaing.arsw.pizarraBack.domain.ports.UserService;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TicketControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TicketController ticketController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("user1", "pass");
        when(userService.validateUser(any(LoginRequest.class))).thenReturn("token123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.ticket", is("token123")));
    }

    @Test
    void login_Error() throws Exception {
        LoginRequest request = new LoginRequest("user1", "wrong");
        when(userService.validateUser(any(LoginRequest.class))).thenThrow(new RuntimeException("Error login"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Error login")));
    }

    @Test
    void register_Success() throws Exception {
        LoginRequest request = new LoginRequest("newUser", "password");
        User user = new User("id123", "newUser", "password", "ROLE_USER");
        when(userService.registerUser(any(LoginRequest.class))).thenReturn(user);

        mockMvc.perform(post("/Register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is("id123")))
                .andExpect(jsonPath("$.username", is("newUser")))
                .andExpect(jsonPath("$.password", is("password")))
                .andExpect(jsonPath("$.role", is("ROLE_USER")));
    }

    @Test
    void register_Error() throws Exception {
        LoginRequest request = new LoginRequest("existingUser", "password");
        when(userService.registerUser(any(LoginRequest.class))).thenThrow(new RuntimeException("Usuario ya existe"));

        mockMvc.perform(post("/Register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Usuario ya existe")));
    }
}
