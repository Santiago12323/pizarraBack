package com.escuelaing.arsw.pizarraBack.infrastructure.controller;
import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
import com.escuelaing.arsw.pizarraBack.domain.ports.UserService;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String ticket = service.validateUser(loginRequest);
            return ResponseEntity.accepted().body(Map.of("ticket",ticket));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error" , e.getMessage()));
        }
    }

    @PostMapping("/Register")
    public ResponseEntity<?> register(@RequestBody LoginRequest loginRequest) {
        try {
            User user = service.registerUser(loginRequest);
            return ResponseEntity.accepted().body(user);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error" , e.getMessage()));
        }
    }
}
