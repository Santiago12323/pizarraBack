package com.escuelaing.arsw.pizarraBack.domain.entity;


import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        User user = new User("id123", "user1", "pass", "ROLE_USER");
        assertEquals("id123", user.getId());
        assertEquals("user1", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = new User();
        user.setId("id456");
        user.setUsername("user2");
        user.setPassword("pass2");
        user.setRole("ROLE_ADMIN");

        assertEquals("id456", user.getId());
        assertEquals("user2", user.getUsername());
        assertEquals("pass2", user.getPassword());
        assertEquals("ROLE_ADMIN", user.getRole());
    }

    @Test
    void testCustomConstructor() {
        User user = new User("user3", "pass3", "ROLE_GUEST");
        assertNull(user.getId());
        assertEquals("user3", user.getUsername());
        assertEquals("pass3", user.getPassword());
        assertEquals("ROLE_GUEST", user.getRole());
    }
}
