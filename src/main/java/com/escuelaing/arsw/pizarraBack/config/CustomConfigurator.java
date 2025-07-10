package com.escuelaing.arsw.pizarraBack.config;

import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomConfigurator extends ServerEndpointConfig.Configurator {

    @Autowired
    private TicketService ticketService;


    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        List<String> tickets = request.getParameterMap().get("ticket");
        if (tickets == null || tickets.isEmpty() || !ticketService.validateTicket(tickets.get(0))) {
            throw new RuntimeException("Ticket inválido");
        }
        sec.getUserProperties().put("userTicket", tickets.get(0));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
