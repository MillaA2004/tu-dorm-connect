package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {


    @Query("""
           select c from Chat c
           join c.members m
           where m.user.id = :userId
           """)
    List<Chat> findAllByUserId(@Param("userId") Long userId);


    @Query("""
           select c from Chat c
           join c.members m1
           join c.members m2
           where c.groupChat = false
             and m1.user.id = :user1Id
             and m2.user.id = :user2Id
           """)
    Optional<Chat> findDirectChatBetweenUsers(@Param("user1Id") Long user1Id,
                                              @Param("user2Id") Long user2Id);
}

