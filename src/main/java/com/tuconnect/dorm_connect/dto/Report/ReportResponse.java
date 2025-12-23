package com.tuconnect.dorm_connect.dto.Report;

import com.tuconnect.dorm_connect.model.ReportTargetType;
import java.time.LocalDateTime;

public record ReportResponse(
    Long reportId,
    Long targetId,
    ReportTargetType targetType,
    String reason,
    Long reporterId,
    LocalDateTime createdAt
) {}