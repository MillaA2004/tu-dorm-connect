package com.tuconnect.dorm_connect.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final LocalDateTime suspendedUntil;

    public UserPrincipal(Long id,
                         String email,
                         String password,
                         Collection<? extends GrantedAuthority> authorities,
                         LocalDateTime suspendedUntil) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.suspendedUntil = suspendedUntil;
    }

    public static boolean isSuspended(LocalDateTime suspendedUntil) {
        return suspendedUntil != null && suspendedUntil.isAfter(LocalDateTime.now());
    }

    public boolean isSuspendedNow() {
        return isSuspended(this.suspendedUntil);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isSuspendedNow();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}