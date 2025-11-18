package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Dorm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DormRepository extends JpaRepository<Dorm, Long> {
    Optional<Dorm> findByName(String name);
}
