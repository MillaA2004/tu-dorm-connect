package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Dorm.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormResponseDTO;
import com.tuconnect.dorm_connect.dto.Dorm.DormUpdateRequestDTO;
import com.tuconnect.dorm_connect.service.DormService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/dorms")
public class DormController {

    private final DormService dormService;

    @Autowired
    public DormController(DormService dormService) {
        this.dormService = dormService;
    }

    @GetMapping
    public ResponseEntity<List<DormResponseDTO>> getAllDorms() {
        return ResponseEntity.ok(dormService.getAllDorms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DormResponseDTO> getDormById(@PathVariable Long id) {
        return ResponseEntity.ok(dormService.getDormById(id));
    }

    @PostMapping(value = "/dorms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DormResponseDTO createDorm(
            @RequestPart("dto") DormRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication
    ) throws IOException {
        return dormService.createDorm(dto, authentication, files);
    }


    @PutMapping(value="/dorms/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DormResponseDTO updateDorm(
            @PathVariable Long id,
            @RequestPart("dto") DormUpdateRequestDTO dto,
            @RequestPart(value="files", required=false) List<MultipartFile> files,
            Authentication authentication
    ) throws IOException {
        return dormService.updateDorm(id, dto, authentication, files);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDorm(@PathVariable Long id) {
        dormService.deleteDorm(id);
        return ResponseEntity.noContent().build();
    }


}
