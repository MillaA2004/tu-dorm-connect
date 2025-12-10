package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.UserMatch;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Transactional
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
    List<UserMatch> findByViewer(User viewer);
    @Modifying
    @Query("DELETE FROM UserMatch um WHERE um.viewer = :viewer")
    void deleteByViewer(@Param("viewer") User viewer);
}
