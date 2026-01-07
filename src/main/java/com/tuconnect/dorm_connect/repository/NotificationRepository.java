package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.actor
        WHERE n.recipient.id = :userId
        ORDER BY n.createdAt DESC
    """)
    List<Notification> findLatestByRecipientWithActor(@Param("userId") Long userId, Pageable pageable);


    long countByRecipient_IdAndReadAtIsNull(Long recipientId);
}
