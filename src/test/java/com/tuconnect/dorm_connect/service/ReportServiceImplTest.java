package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Report.ReportRequest;
import com.tuconnect.dorm_connect.dto.Report.ReportResponse;
import com.tuconnect.dorm_connect.model.Report;
import com.tuconnect.dorm_connect.model.ReportTargetType;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.*;
import com.tuconnect.dorm_connect.service.implementations.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User reporter;

    @BeforeEach
    void setUp() {
        reporter = new User();
        reporter.setId(1L);
    }

    @Test
    void submitReport_Success() {
        ReportRequest request = new ReportRequest(10L, ReportTargetType.POST, "Inappropriate content");
        
        when(postRepository.existsById(10L)).thenReturn(true);
        when(reportRepository.existsByReportedEntityIdAndReportedEntityTypeAndReporterId(10L, ReportTargetType.POST, 1L))
                .thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
        
        Report savedReport = Report.builder()
                .id(100L)
                .reportedEntityId(10L)
                .reportedEntityType(ReportTargetType.POST)
                .reason("Inappropriate content")
                .reporter(reporter)
                .build();
        
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponse response = reportService.submitReport(request, 1L);

        assertNotNull(response);
        assertEquals(100L, response.reportId());
        assertEquals(10L, response.targetId());
        assertEquals(ReportTargetType.POST, response.targetType());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void submitReport_DuplicateReport_ThrowsException() {
        ReportRequest request = new ReportRequest(10L, ReportTargetType.POST, "Spam");

        when(postRepository.existsById(10L)).thenReturn(true);
        when(reportRepository.existsByReportedEntityIdAndReportedEntityTypeAndReporterId(10L, ReportTargetType.POST, 1L))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> reportService.submitReport(request, 1L));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void submitReport_TargetNotFound_ThrowsException() {
        ReportRequest request = new ReportRequest(999L, ReportTargetType.EVENT, "Non-existent");

        when(eventRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> reportService.submitReport(request, 1L));
    }
}