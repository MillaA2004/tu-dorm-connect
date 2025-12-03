package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
    List<UserMatch> findByViewer(User viewer);
}
