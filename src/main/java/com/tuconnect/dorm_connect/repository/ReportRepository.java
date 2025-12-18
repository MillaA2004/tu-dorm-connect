package com.tuconnect.dorm_connect.repository;

import com.tuconnect.dorm_connect.model.Report;
import com.tuconnect.dorm_connect.model.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReportedEntityIdAndReportedEntityTypeAndReporterId(Long entityId, ReportTargetType type, Long reporterId);
}