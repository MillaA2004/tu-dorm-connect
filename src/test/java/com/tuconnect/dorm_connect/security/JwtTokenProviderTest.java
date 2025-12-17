package com.tuconnect.dorm_connect.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "9a4f2c8d3b7a1e6f4g5h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d"; // Same as properties for test
    private final long expiration = 3600000;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationDate", expiration);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = jwtTokenProvider.generateToken(authentication);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void getUsername_ShouldReturnCorrectUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = jwtTokenProvider.generateToken(authentication);

        String username = jwtTokenProvider.getEmail(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsMalformed() {
        String malformedToken = "invalid.token.structure";
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);
        assertFalse(isValid);
    }
}