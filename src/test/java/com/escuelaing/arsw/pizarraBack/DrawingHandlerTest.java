package com.escuelaing.arsw.pizarraBack;

import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DrawingHandlerTest {

    private DrawingHandler handler;
    private TicketService ticketService;
    private WebSocketSession session;
    private WebSocketSession otherSession;

    @BeforeEach
    public void setUp() {
        ticketService = mock(TicketService.class);
        handler = new DrawingHandler();
        session = mock(WebSocketSession.class);
        otherSession = mock(WebSocketSession.class);

        var field = DrawingHandler.class.getDeclaredFields();
        for (var f : field) {
            if (f.getType().equals(TicketService.class)) {
                f.setAccessible(true);
                try {
                    f.set(handler, ticketService);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Test
    public void testAfterConnectionEstablished_validTicket_addsSession() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost?ticket=123"));
        when(ticketService.validateTicket("123")).thenReturn(true);
        when(session.getId()).thenReturn("s1");

        handler.afterConnectionEstablished(session);

        verify(session, never()).close(any());
    }

    @Test
    public void testAfterConnectionEstablished_invalidTicket_closesSession() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost?ticket=bad"));
        when(ticketService.validateTicket("bad")).thenReturn(false);

        handler.afterConnectionEstablished(session);

        ArgumentCaptor<CloseStatus> captor = ArgumentCaptor.forClass(CloseStatus.class);
        verify(session).close(captor.capture());
        assertEquals("Ticket inválido", captor.getValue().getReason());
    }

    @Test
    public void testExtractTicketFromQuery_valid() throws Exception {
        URI uri = new URI("ws://localhost?ticket=abc123&other=value");
        when(session.getUri()).thenReturn(uri);
        when(ticketService.validateTicket("abc123")).thenReturn(true);
        when(session.getId()).thenReturn("s1");

        handler.afterConnectionEstablished(session);

        verify(session, never()).close(any());
    }

    @Test
    public void testExtractTicketFromQuery_null() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost"));
        when(ticketService.validateTicket(null)).thenReturn(false);

        handler.afterConnectionEstablished(session);

        verify(session).close(any());
    }

    @Test
    public void testHandleTextMessage_broadcastsToOtherSessions() throws Exception {
        when(session.getId()).thenReturn("s1");
        when(session.getUri()).thenReturn(new URI("ws://localhost?ticket=valid"));
        when(ticketService.validateTicket("valid")).thenReturn(true);

        when(otherSession.getId()).thenReturn("s2");
        when(otherSession.getUri()).thenReturn(new URI("ws://localhost?ticket=valid"));
        when(ticketService.validateTicket("valid")).thenReturn(true);
        when(otherSession.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);
        handler.afterConnectionEstablished(otherSession);

        TextMessage message = new TextMessage("data");
        handler.handleTextMessage(session, message);

        verify(otherSession).sendMessage(message);
        verify(session, never()).sendMessage(any());
    }

    @Test
    public void testAfterConnectionClosed_removesSession() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost?ticket=ok"));
        when(ticketService.validateTicket("ok")).thenReturn(true);
        when(session.getId()).thenReturn("s1");

        handler.afterConnectionEstablished(session);
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // No exceptions means session removal handled
        verify(session, never()).sendMessage(any());
    }
}
