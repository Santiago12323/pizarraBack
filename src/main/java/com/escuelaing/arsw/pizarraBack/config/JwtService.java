package com.escuelaing.arsw.pizarraBack.config;

import com.escuelaing.arsw.pizarraBack.domain.ports.UserService;
import com.escuelaing.arsw.pizarraBack.infrastructure.controller.RefreshTokenRequest;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.UserRepository;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    @Value("${jwt.signature}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMillis;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    @Autowired
    private UserRepository repository;


    public String generateToken(User user) {
        return buildToken(user, expirationMillis);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMillis);
    }

    private String buildToken(User user, long expirationTime) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractName(String token) {
        return extractAllClaims(token).getId();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> buildResponseRefreshToken(RefreshTokenRequest refreshToken) throws IOException {
        if (!isTokenValid(refreshToken.getRefresToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String name = extractName(refreshToken.getRefresToken());
        User user = repository.findByUsername(name).orElseThrow(() -> new RuntimeException("no found "));

        String newAccessToken = buildToken(user, expirationMillis);

        String newRefreshToken = generateRefreshToken(user);

        return Map.of(
                "access_token", newAccessToken,
                "refresh_token", newRefreshToken,
                "user_id", user.getId()
        );

    }

    public Map<String,String> buildResponseLogin(User user){
        Map<String,String> response = new HashMap<>();
        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("role", String.valueOf(user.getRole()));
        response.put("id", String.valueOf(user.getId()));

        return response;
    }
}
