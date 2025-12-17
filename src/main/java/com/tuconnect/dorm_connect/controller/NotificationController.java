package com.tuconnect.dorm_connect.controller;


import com.tuconnect.dorm_connect.dto.notification.NotificationDTO;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.implementations.NotificationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServiceImpl notificationService;
    private final UserRepository userRepository;


    @GetMapping
    public List<NotificationDTO> getMyNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = getCurrentUserId(authentication);
        return notificationService.getMyNotifications(userId, page, size);
    }


    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        long count = notificationService.getUnreadCount(userId);
        return Map.of("unreadCount", count);
    }


    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        notificationService.markAsRead(id, userId);
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();
    }
}
