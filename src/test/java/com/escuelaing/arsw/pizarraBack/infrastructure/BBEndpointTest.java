package com.escuelaing.arsw.pizarraBack.infrastructure;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import com.escuelaing.arsw.pizarraBack.infrastructure.controller.BBEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BBEndpointTest {

    private BBEndpoint endpoint;
    private Session session1;
    private Session session2;
    private RemoteEndpoint.Basic remote1;
    private RemoteEndpoint.Basic remote2;

    @BeforeEach
    public void setUp() {
        endpoint = new BBEndpoint();
        session1 = mock(Session.class);
        session2 = mock(Session.class);
        remote1 = mock(RemoteEndpoint.Basic.class);
        remote2 = mock(RemoteEndpoint.Basic.class);
        when(session1.getId()).thenReturn("s1");
        when(session2.getId()).thenReturn("s2");
        when(session1.getBasicRemote()).thenReturn(remote1);
        when(session2.getBasicRemote()).thenReturn(remote2);
    }

    @Test
    public void testOnOpenAddsSessionAndSendsMessage() throws IOException {
        endpoint.onOpen(session1);
        verify(remote1).sendText("Conexión establecida.");
    }

    @Test
    public void testOnMessageBroadcastsToOtherSessions() throws IOException {
        endpoint.onOpen(session1);
        endpoint.onOpen(session2);
        endpoint.onMessage("Hola", session1);
        verify(remote2).sendText("Hola");
        verify(remote1, never()).sendText("Hola");
    }

    @Test
    public void testOnCloseRemovesSession() throws IOException {
        endpoint.onOpen(session1);
        endpoint.onClose(session1);
        endpoint.onOpen(session2);

        clearInvocations(remote1, remote2);

        endpoint.onMessage("Mensaje", session2);

        verify(remote2, never()).sendText("Mensaje");
        verify(remote1, never()).sendText(anyString());
    }

    @Test
    public void testOnErrorRemovesSession() throws IOException {
        endpoint.onOpen(session1);
        Throwable error = new RuntimeException("Error de prueba");
        endpoint.onError(session1, error);
        endpoint.onOpen(session2);

        clearInvocations(remote1, remote2);

        endpoint.onMessage("Hola", session2);

        verify(remote2, never()).sendText("Hola");
        verify(remote1, never()).sendText(anyString());
    }

    @Test
    public void testBroadcastHandlesIOException() throws IOException {
        endpoint.onOpen(session1);
        endpoint.onOpen(session2);
        doThrow(new IOException("Error simulada")).when(remote2).sendText("Hola");
        endpoint.onMessage("Hola", session1);
        verify(remote2).sendText("Hola");
    }
}
