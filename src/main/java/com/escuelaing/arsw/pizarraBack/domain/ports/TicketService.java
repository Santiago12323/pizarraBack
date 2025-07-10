package com.escuelaing.arsw.pizarraBack.domain.ports;

public interface TicketService {
    void storeTicket(String ticket, String username);

    boolean validateTicket(String ticket);
}
