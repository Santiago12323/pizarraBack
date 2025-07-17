package com.escuelaing.arsw.pizarraBack.infrastructure.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class LoginRequest {
    private String username;
    private String password;
}
