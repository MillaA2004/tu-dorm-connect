package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Report.ReportRequest;
import com.tuconnect.dorm_connect.dto.Report.ReportResponse;
import java.util.List;

public interface ReportService {
    ReportResponse submitReport(ReportRequest request, Long reporterId);
    List<ReportResponse> getAllReports();
}