package com.escuelaing.arsw.pizarraBack.config;

import com.escuelaing.arsw.pizarraBack.infrastructure.controller.RefreshTokenRequest;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.UserRepository;
import com.escuelaing.arsw.pizarraBack.infrastructure.repository.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserRepository userRepository;

    private final String secretKey = "L3OaW5Jv7f9qT8U0pD7rYkJ9u3PxMfBcXlZAnKj2hFs=";
    private final long expirationMillis = 1000 * 60 * 60; // 1h
    private final long refreshExpirationMillis = 1000 * 60 * 60 * 24;

    @BeforeEach
    public void setUp() throws Exception {
        jwtService = new JwtService();
        userRepository = mock(UserRepository.class);

        var secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, secretKey);

        var expField = JwtService.class.getDeclaredField("expirationMillis");
        expField.setAccessible(true);
        expField.set(jwtService, expirationMillis);

        var refreshExpField = JwtService.class.getDeclaredField("refreshExpirationMillis");
        refreshExpField.setAccessible(true);
        refreshExpField.set(jwtService, refreshExpirationMillis);

        var repoField = JwtService.class.getDeclaredField("repository");
        repoField.setAccessible(true);
        repoField.set(jwtService, userRepository);
    }

    private User createUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ADMIN");
        user.setId(String.valueOf(42L));
        return user;
    }

    @Test
    public void testGenerateAndExtractToken() {
        User user = createUser();
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("testUser", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
    }

    @Test
    public void testExtractRole() {
        User user = createUser();
        String token = jwtService.generateToken(user);
        String role = jwtService.extractRole(token);
        assertEquals("ADMIN", role);
    }

    @Test
    public void testIsTokenValid_validToken() {
        User user = createUser();
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    public void testIsTokenValid_invalidToken() {
        String token = "invalid.token.value";
        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    public void testBuildResponseLogin() {
        User user = createUser();
        Map<String, String> response = jwtService.buildResponseLogin(user);

        assertNotNull(response.get("accessToken"));
        assertNotNull(response.get("refreshToken"));
        assertEquals("ADMIN", response.get("role"));
        assertEquals("42", response.get("id"));
    }

    @Test
    public void testBuildResponseRefreshToken_validToken() throws IOException {
        User user = createUser();
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefresToken(refreshToken);

        String modifiedToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setId(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        request.setRefresToken(modifiedToken);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Map<String, Object> result = jwtService.buildResponseRefreshToken(request);

        assertNotNull(result.get("access_token"));
        assertNotNull(result.get("refresh_token"));
        assertEquals("42", result.get("user_id").toString());
    }


    @Test
    public void testBuildResponseRefreshToken_invalidToken_throwsException() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefresToken("invalid.token");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            jwtService.buildResponseRefreshToken(request);
        });

        assertEquals("Invalid refresh token", ex.getMessage());
    }
}
