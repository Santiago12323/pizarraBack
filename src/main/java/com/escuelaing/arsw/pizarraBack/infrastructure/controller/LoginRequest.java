package com.escuelaing.arsw.pizarraBack.infrastructure.controller;

import lombok.Data;

@Data
public  class LoginRequest {
    private String username;
    private String password;
}
