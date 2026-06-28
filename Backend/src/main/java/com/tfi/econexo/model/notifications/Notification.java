package com.tfi.econexo.model.notifications;

import com.tfi.econexo.model.auth.UserSec;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserSec user;

    private String message;

    private boolean isRead;

    private LocalDateTime createdAt = LocalDateTime.now();
}
