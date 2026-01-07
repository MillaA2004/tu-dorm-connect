package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ListingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/listings")
@Validated
public class ListingController {

    private final ListingService listingService;
    private final UserRepository userRepository;

    @PostMapping("/poster/{posterId}")
    public ResponseEntity<ListingResponseDTO> createListing(
            @PathVariable Long posterId,
            @Valid @RequestBody ListingRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.createListing(posterId, dto));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ListingResponseDTO>> getActiveListings(
            @RequestParam(required = false) Long viewerId) {

        if (viewerId != null) {
            return ResponseEntity.ok(listingService.getCompatibleListings(viewerId));
        } else {
            return ResponseEntity.ok(listingService.getActiveListings());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> getListingById(
            @PathVariable @Positive Long id){
        return new ResponseEntity<>(listingService.getListingById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ListingResponseDTO>> getListingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(listingService.getListingsByUserId(userId));
    }

    @GetMapping("/dormName/{dormName}")
    public ResponseEntity<List<ListingResponseDTO>> getListingsByDorm(@PathVariable String dormName) {
        return ResponseEntity.ok(listingService.getListingsByDorm(dormName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> updateListing(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ListingRequestDTO listingRequestDTO,
            @RequestParam Long currentUserId) {

        ListingResponseDTO updated = listingService.updateListing(id, listingRequestDTO, currentUserId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable @Positive Long id,
            @RequestParam Long currentUserId) {

        listingService.deleteListing(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ListingResponseDTO>> searchListings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long viewerId
    ) {
        List<ListingResponseDTO> listings = listingService.searchListings(keyword, viewerId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/price/max")
    public ResponseEntity<List<ListingResponseDTO>> getListingsByPriceMax(@RequestParam Double maxPrice) {
        return ResponseEntity.ok(listingService.getListingsByPriceMax(maxPrice));
    }
}
