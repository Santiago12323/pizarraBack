    package com.escuelaing.arsw.pizarraBack.domain.usecases;

    import com.escuelaing.arsw.pizarraBack.domain.ports.TicketService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.stereotype.Service;

    import java.time.Duration;

    @Service
    public class TicketServiceImpl implements TicketService {

        @Autowired
        private StringRedisTemplate redisTemplate;

        private static final String PREFIX = "ws:ticket:";

        @Override
        public void storeTicket(String ticket, String username) {
            redisTemplate.opsForValue().set(PREFIX + ticket, username, Duration.ofMinutes(10));
            System.out.println("Ticket guardado: " + ticket);
        }


        @Override
        public boolean validateTicket(String ticket) {
            Boolean hasKey = redisTemplate.hasKey(PREFIX + ticket);
            System.out.println("Validando ticket: " + ticket + " -> Existe: " + hasKey);
            return hasKey != null && hasKey;
        }



    }
