package com.tuconnect.dorm_connect.repository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import com.tuconnect.dorm_connect.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCreator_Id(Long userId);

    List<Event> findAllByParticipants_Id(Long userId);

    @Query("""
        SELECT e FROM Event e
        WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(e.address) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY e.dateTime ASC
    """)
    List<Event> searchByTitleOrAddress(@Param("q") String q);


    List<Event> findAllByDateTimeAfterOrderByDateTimeAsc(LocalDateTime cutoff);
}
