package com.tfi.econexo.controller.notification;

import com.tfi.econexo.dto.donation.NotificationResponseDTO;
import com.tfi.econexo.utils.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Notifications", description = "Endpoints for notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread/count")
    @Operation(summary = "Count unread notifications", description = "Return the amount of pending notifications")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        String email = authentication.getName();

        return ResponseEntity.ok(notificationService.countUnreadNotifications(email));
    }

    @GetMapping
    @Operation(summary = "List notifications", description = "Return the user history notifications.")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getNotifications(email)
                .stream()
                .map(n -> new NotificationResponseDTO(n.getId(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList());
    }

    @PutMapping("/read")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the authenticated user.")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal.getName());
        return ResponseEntity.ok().build();
    }
}
