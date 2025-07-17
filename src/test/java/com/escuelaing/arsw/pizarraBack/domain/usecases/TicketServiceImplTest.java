package com.escuelaing.arsw.pizarraBack.domain.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.escuelaing.arsw.pizarraBack.domain.usecases.TicketServiceImpl;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void storeTicket_ShouldStoreValueWithExpiration() {
        String ticket = "ticket123";
        String username = "user1";

        ticketService.storeTicket(ticket, username);

        verify(valueOperations).set(eq("ws:ticket:" + ticket), eq(username), eq(Duration.ofMinutes(10)));
    }

    @Test
    void validateTicket_WhenExists_ShouldReturnTrue() {
        String ticket = "ticket123";

        when(redisTemplate.hasKey("ws:ticket:" + ticket)).thenReturn(true);

        assertTrue(ticketService.validateTicket(ticket));
        verify(redisTemplate).hasKey("ws:ticket:" + ticket);
    }

    @Test
    void validateTicket_WhenDoesNotExist_ShouldReturnFalse() {
        String ticket = "ticket123";

        when(redisTemplate.hasKey("ws:ticket:" + ticket)).thenReturn(false);

        assertFalse(ticketService.validateTicket(ticket));
        verify(redisTemplate).hasKey("ws:ticket:" + ticket);
    }

    @Test
    void validateTicket_WhenHasKeyReturnsNull_ShouldReturnFalse() {
        String ticket = "ticket123";

        when(redisTemplate.hasKey("ws:ticket:" + ticket)).thenReturn(null);

        assertFalse(ticketService.validateTicket(ticket));
        verify(redisTemplate).hasKey("ws:ticket:" + ticket);
    }

    @Test
    void storeTicket_ShouldHandleNullUsername() {
        String ticket = "ticket123";
        String username = null;

        ticketService.storeTicket(ticket, username);

        verify(valueOperations).set(eq("ws:ticket:" + ticket), isNull(), eq(Duration.ofMinutes(10)));
    }

    @Test
    void validateTicket_WithEmptyTicket_ShouldReturnFalse() {
        assertFalse(ticketService.validateTicket(""));
    }

    @Test
    void validateTicket_WithNullTicket_ShouldReturnFalse() {
        assertFalse(ticketService.validateTicket(null));
    }
}
