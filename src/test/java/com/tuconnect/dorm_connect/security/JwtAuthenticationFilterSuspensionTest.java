package com.tuconnect.dorm_connect.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterSuspensionTest {

    @Test
    void doFilterInternal_ShouldReturn403_WhenUserSuspended() throws Exception {
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test/db-connection");
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtTokenProvider.getUsername("valid-token")).thenReturn("suspended@test.com");

        UserPrincipal principal = new UserPrincipal(
                1L,
                "suspended@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("User")),
                LocalDateTime.now().plusMinutes(5)
        );

        when(userDetailsService.loadUserByUsername("suspended@test.com")).thenReturn(principal);

        filter.doFilter(request, response, chain);

        assertEquals(403, response.getStatus());
        verify(chain, never()).doFilter(any(), any());
    }
}