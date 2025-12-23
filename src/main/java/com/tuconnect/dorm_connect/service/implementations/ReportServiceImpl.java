package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Report.ReportRequest;
import com.tuconnect.dorm_connect.dto.Report.ReportResponse;
import com.tuconnect.dorm_connect.model.Report;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.*;
import com.tuconnect.dorm_connect.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ReportResponse submitReport(ReportRequest request, Long reporterId) {
        validateTargetExists(request);

        if (reportRepository.existsByReportedEntityIdAndReportedEntityTypeAndReporterId(
                request.targetId(), request.targetType(), reporterId)) {
            throw new RuntimeException("You have already reported this " + request.targetType());
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        Report report = Report.builder()
                .reportedEntityId(request.targetId())
                .reportedEntityType(request.targetType())
                .reason(request.reason())
                .reporter(reporter)
                .build();

        Report saved = reportRepository.save(report);
        return mapToResponse(saved);
    }


    @Override
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void markReportAsViewed(Long reportId) {
        var report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setIsViewed(true);
        reportRepository.save(report);
    }


    private void validateTargetExists(ReportRequest request) {
        boolean exists = switch (request.targetType()) {
            case POST -> postRepository.existsById(request.targetId());
            case EVENT -> eventRepository.existsById(request.targetId());
            case USER -> userRepository.existsById(request.targetId());
        };

        if (!exists) {
            throw new RuntimeException(request.targetType() + " not found with ID: " + request.targetId());
        }
    }

    private ReportResponse mapToResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReportedEntityId(),
                report.getReportedEntityType(),
                report.getReason(),
                report.getReporter().getId(),
                report.getCreatedAt(),
                report.getIsViewed()
        );
    }
}