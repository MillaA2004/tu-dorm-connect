package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.DormDTO;
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

    // Get all dorms
    @GetMapping
    public ResponseEntity<List<DormDTO>> getAllDorms() {
        return ResponseEntity.ok(dormService.getAllDorms());
    }

    // Get dorm by ID
    @GetMapping("/{id}")
    public ResponseEntity<DormDTO> getDormById(@PathVariable Long id) {
        return ResponseEntity.ok(dormService.getDormById(id));
    }

    // Create new dorm
    @PostMapping
    public ResponseEntity<DormDTO> createDorm(@RequestBody DormDTO dormDTO) {
        return ResponseEntity.ok(dormService.createDorm(dormDTO));
    }

    // Update dorm
    @PutMapping("/{id}")
    public ResponseEntity<DormDTO> updateDorm(
            @PathVariable Long id,
            @RequestBody DormDTO updatedDormDTO
    ) {
        return ResponseEntity.ok(dormService.updateDorm(id, updatedDormDTO));
    }

    // Delete dorm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDorm(@PathVariable Long id) {
        dormService.deleteDorm(id);
        return ResponseEntity.noContent().build();
    }

    // Search by name
    @GetMapping("/search")
    public ResponseEntity<DormDTO> getDormByName(@RequestParam String name) {
        return ResponseEntity.ok(dormService.getDormByName(name));
    }

    // Filter: max price
    @GetMapping("/filter/price-max")
    public ResponseEntity<List<DormDTO>> getDormsByPriceMax(@RequestParam Double maxPrice) {
        return ResponseEntity.ok(dormService.getDormsByPriceMax(maxPrice));
    }

    // Filter: block number
    @GetMapping("/filter/block")
    public ResponseEntity<List<DormDTO>> getDormsByBlock(@RequestParam String block) {
        return ResponseEntity.ok(dormService.getDormsByBlock(block));
    }

    // Search keyword
    @GetMapping("/search/keyword")
    public ResponseEntity<List<DormDTO>> searchDorms(@RequestParam String q) {
        return ResponseEntity.ok(dormService.searchDorms(q));
    }

    // Filter: price range
    @GetMapping("/filter/price-range")
    public ResponseEntity<List<DormDTO>> getDormsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max
    ) {
        return ResponseEntity.ok(dormService.getDormsByPriceRange(min, max));
    }
}
