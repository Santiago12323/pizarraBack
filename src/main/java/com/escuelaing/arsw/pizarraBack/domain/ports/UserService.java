package com.escuelaing.arsw.pizarraBack.domain.ports;

import com.escuelaing.arsw.pizarraBack.infrastructure.controller.LoginRequest;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;

public interface UserService {
    String validateUser(LoginRequest request);
    User registerUser(LoginRequest request);

    User findByName(String id);
}
