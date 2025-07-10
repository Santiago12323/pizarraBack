package com.escuelaing.arsw.pizarraBack;


import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DrawingHandler extends TextWebSocketHandler {

    private Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    @Autowired
    private TicketService ticketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String ticket = extractTicketFromQuery(query);

        if (!ticketService.validateTicket(ticket)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Ticket inválido"));
            return;
        }

        sessions.add(session);
        System.out.println("Nueva conexión WebSocket válida: " + session.getId());
    }

    private String extractTicketFromQuery(String query) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals("ticket")) {
                return pair[1];
            }
        }
        return null;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (WebSocketSession ws : sessions) {
            if (ws.isOpen() && !ws.getId().equals(session.getId())) {
                ws.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Conexión cerrada: " + session.getId());
    }
}