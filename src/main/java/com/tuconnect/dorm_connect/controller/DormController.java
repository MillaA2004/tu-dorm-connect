package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.DormRequestDTO;
import com.tuconnect.dorm_connect.dto.DormResponseDTO;
import com.tuconnect.dorm_connect.service.DormService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<DormResponseDTO> createDorm(@RequestBody DormRequestDTO dto) {
        return ResponseEntity.ok(dormService.createDorm(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DormResponseDTO> updateDorm(
            @PathVariable Long id,
            @RequestBody DormRequestDTO dto
    ) {
        return ResponseEntity.ok(dormService.updateDorm(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDorm(@PathVariable Long id) {
        dormService.deleteDorm(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<DormResponseDTO> getDormByName(@RequestParam String name) {
        return ResponseEntity.ok(dormService.getDormByName(name));
    }

    @GetMapping("/filter/price-max")
    public ResponseEntity<List<DormResponseDTO>> getDormsByPriceMax(@RequestParam Double maxPrice) {
        return ResponseEntity.ok(dormService.getDormsByPriceMax(maxPrice));
    }

    @GetMapping("/filter/block")
    public ResponseEntity<List<DormResponseDTO>> getDormsByBlock(@RequestParam String block) {
        return ResponseEntity.ok(dormService.getDormsByBlock(block));
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<List<DormResponseDTO>> searchDorms(@RequestParam String q) {
        return ResponseEntity.ok(dormService.searchDorms(q));
    }

    @GetMapping("/filter/price-range")
    public ResponseEntity<List<DormResponseDTO>> getDormsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max
    ) {
        return ResponseEntity.ok(dormService.getDormsByPriceRange(min, max));
    }
}
