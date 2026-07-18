package com.tfi.econexo.repository.notification;

import com.tfi.econexo.model.notifications.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByUser_EmailAndIsReadFalse(String email);

    List<Notification> findByUser_EmailOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.email = :email")
    void markAllAsReadByUserEmail(@Param("email") String email);

    void deleteByUser_Email(String userEmail);
}
