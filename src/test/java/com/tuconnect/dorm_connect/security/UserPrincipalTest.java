package com.tuconnect.dorm_connect.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    @Test
    void isSuspendedNow_ShouldBeTrue_WhenSuspendedUntilIsInFuture() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "user@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("User")),
                LocalDateTime.now().plusMinutes(10)
        );

        assertTrue(principal.isSuspendedNow());
        assertFalse(principal.isAccountNonLocked());
    }

    @Test
    void isSuspendedNow_ShouldBeFalse_WhenSuspendedUntilIsNull() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "user@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("User")),
                null
        );

        assertFalse(principal.isSuspendedNow());
        assertTrue(principal.isAccountNonLocked());
    }

    @Test
    void isSuspendedNow_ShouldBeFalse_WhenSuspendedUntilIsInPast() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "user@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("User")),
                LocalDateTime.now().minusMinutes(1)
        );

        assertFalse(principal.isSuspendedNow());
        assertTrue(principal.isAccountNonLocked());
    }
}