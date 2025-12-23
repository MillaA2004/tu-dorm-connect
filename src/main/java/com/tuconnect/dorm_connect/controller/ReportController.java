package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Report.ReportRequest;
import com.tuconnect.dorm_connect.dto.Report.ReportResponse;
import com.tuconnect.dorm_connect.security.UserPrincipal;
import com.tuconnect.dorm_connect.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> submitReport(
            @Valid @RequestBody ReportRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        ReportResponse response = reportService.submitReport(request, userPrincipal.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }
}