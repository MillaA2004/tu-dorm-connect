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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "testSecretKeyWithEnoughLengthForHS256Algorithm123456";
    private final String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", base64Secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationDate", 3600000L);
    }

    @Test
    void getUsername_ShouldReturnCorrectUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = jwtTokenProvider.generateToken(authentication);

        String username = jwtTokenProvider.getUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        // Create an already expired token
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret)), SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_shouldReturnFalseForMalformedToken() {
        boolean isValid = jwtTokenProvider.validateToken("not.a.valid.token");
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidSignature() {
        String differentSecret = Base64.getEncoder().encodeToString("differentSecretKeyForTestingPurposesOnly12345".getBytes());
        String tokenWithDifferentKey = Jwts.builder()
                .setSubject("user")
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(differentSecret)), SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentKey);

        assertThat(isValid).isFalse();
    }
}