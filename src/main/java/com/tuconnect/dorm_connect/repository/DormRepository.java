package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Dorm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DormRepository extends JpaRepository<Dorm, Long> {

    // find dorm by exact name
    Optional<Dorm> findByName(String name);

    // find dorms with price less than or equal to X
    List<Dorm> findByPriceLessThanEqual(Double maxPrice);

    // find dorms by block number
    List<Dorm> findByBlockNumber(String blockNumber);

    // search dorms containing a keyword in the name
    List<Dorm> findByNameContainingIgnoreCase(String keyword);

    // find dorms within a price range
    List<Dorm> findByPriceBetween(Double minPrice, Double maxPrice);
}