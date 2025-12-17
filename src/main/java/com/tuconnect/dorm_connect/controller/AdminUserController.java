package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.auth.SuspendUserRequest;
import com.tuconnect.dorm_connect.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Long userId, @Valid @RequestBody SuspendUserRequest request) {
        userService.suspendUser(userId, request.minutes());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/{userId}/unsuspend")
    public ResponseEntity<Void> unsuspendUser(@PathVariable Long userId) {
        userService.unsuspendUser(userId);
        return ResponseEntity.noContent().build();
    }
}