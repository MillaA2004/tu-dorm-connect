package com.tuconnect.dorm_connect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderSuspensionTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String BASE64_SECRET = "c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0"; // base64
    private static final long EXPIRATION_MS = 3600000;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", BASE64_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationDate", EXPIRATION_MS);
    }

    @Test
    void generateToken_ShouldIncludeSuspendedClaims_WhenUserIsSuspended() {
        LocalDateTime suspendedUntil = LocalDateTime.now().plusMinutes(30);

        UserPrincipal principal = new UserPrincipal(
                10L,
                "suspended@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("User")),
                suspendedUntil
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(authentication);

        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(BASE64_SECRET));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("suspended@test.com", claims.getSubject());
        assertEquals(true, claims.get("suspended", Boolean.class));

        Long suspendedUntilEpochMs = claims.get("suspendedUntil", Long.class);
        assertNotNull(suspendedUntilEpochMs);

        long expectedMs = suspendedUntil.toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue(Math.abs(expectedMs - suspendedUntilEpochMs) < 2000);
    }

    @Test
    void generateToken_ShouldIncludeSuspendedFalse_WhenUserNotSuspended() {
        UserPrincipal principal = new UserPrincipal(
                11L,
                "ok@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("User")),
                null
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(authentication);

        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(BASE64_SECRET));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(false, claims.get("suspended", Boolean.class));
        assertNull(claims.get("suspendedUntil"));
    }
}