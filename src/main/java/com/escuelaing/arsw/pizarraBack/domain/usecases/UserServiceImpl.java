package com.escuelaing.arsw.pizarraBack.domain.usecases;

import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
import com.escuelaing.arsw.pizarraBack.infrastructure.controller.LoginRequest;
import com.escuelaing.arsw.pizarraBack.domain.ports.UserService;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.UserRepository;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TicketService service;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public String validateUser(LoginRequest request) {
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(passwordEncoder.matches(request.getPassword(), user.getPassword())){
            String ticket = UUID.randomUUID().toString();
            service.storeTicket(ticket, request.getUsername());
            return ticket;
        }

        new RuntimeException("Credenciales incorrectas");

        return null;
    }
    @Override
    public User registerUser(LoginRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return repository.save(user);
    }
}

