package com.escuelaing.arsw.pizarraBack.infrastructure.controller;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/bbService")
public class BBEndpoint {

    private static final Logger logger = Logger.getLogger(BBEndpoint.class.getName());

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        logger.log(Level.INFO, "Nueva conexión abierta: {0}", session.getId());
        try {
            session.getBasicRemote().sendText("Conexión establecida.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error enviando mensaje de conexión", e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.log(Level.INFO, "Mensaje recibido de {0}: {1}", new Object[]{session.getId(), message});
        broadcast(message, session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        logger.log(Level.INFO, "Conexión cerrada: {0}", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        logger.log(Level.SEVERE, "Error en sesión: " + session.getId(), throwable);
    }

    private void broadcast(String message, Session senderSession) {
        sessions.forEach(session -> {
            if (!session.equals(senderSession)) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error enviando mensaje a la sesión: " + session.getId(), e);
                }
            }
        });
    }
}

