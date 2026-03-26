package com.example.helloworld.auth;

import com.example.helloworld.auth.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "dGVzdFNlY3JldEtleUZvckp3dFRlc3RpbmdQdXJwb3Nlc09ubHlMb25nRW5vdWdo";
    private static final long EXPIRATION_MS = 86400000;

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
        userDetails = new User("testuser", "password", List.of());
    }

    @Test
    void generateToken_returnsValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateToken_returnsTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_returnsFalseForWrongUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", List.of());

        assertFalse(jwtService.validateToken(token, otherUser));
    }

    @Test
    void validateToken_throwsForExpiredToken() {
        JwtService shortLivedService = new JwtService(SECRET, -1000);
        String token = shortLivedService.generateToken(userDetails);

        assertThrows(Exception.class, () -> jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_throwsForTamperedToken() {
        String token = jwtService.generateToken(userDetails);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(Exception.class, () -> jwtService.validateToken(tampered, userDetails));
    }

    @Test
    void extractUsername_throwsForTokenSignedWithDifferentKey() {
        String otherSecret = "b3RoZXJTZWNyZXRLZXlGb3JKd3RUZXN0aW5nUHVycG9zZXNPbmx5TG9uZ0Vub3VnaA==";
        String token = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(otherSecret)))
                .compact();

        assertThrows(Exception.class, () -> jwtService.extractUsername(token));
    }
}
