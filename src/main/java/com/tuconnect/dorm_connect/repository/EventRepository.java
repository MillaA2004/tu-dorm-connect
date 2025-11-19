package com.tuconnect.dorm_connect.repository;
import java.util.List;
import com.tuconnect.dorm_connect.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCreator_UserId(Long userId);

    List<Event> findAllByParticipants_UserId(Long userId);
}
