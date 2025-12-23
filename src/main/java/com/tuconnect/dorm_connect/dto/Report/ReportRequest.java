package com.tuconnect.dorm_connect.dto.Report;

import com.tuconnect.dorm_connect.model.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequest(
    @NotNull(message = "Target ID is required")
    Long targetId,

    @NotNull(message = "Target type is required")
    ReportTargetType targetType,

    @NotBlank(message = "Reason is required")
    String reason
) {}