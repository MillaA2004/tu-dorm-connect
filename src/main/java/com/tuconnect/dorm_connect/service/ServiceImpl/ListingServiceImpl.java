package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.mapper.ListingMapper;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.repository.ListingRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ListingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final DormRepository dormRepository;
    private final ListingMapper listingMapper;

    @Override
    @Transactional
    public ListingResponseDTO createListing(ListingRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Dorm dorm = dormRepository.findById(dto.dormId())
                .orElseThrow(() -> new EntityNotFoundException("Dorm not found"));

        Listing listing = listingMapper.toEntity(dto);
        listing.setIsActive(true);
        listing.setUser(user);
        listing.setDorm(dorm);

        Listing saved = listingRepository.save(listing);
        return listingMapper.toResponseDTO(saved);
    }

    @Override
    public ListingResponseDTO getListingById(Long id){
        Listing listing = listingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Listing not found."));
        return listingMapper.toResponseDTO(listing);
    }

//    @Override
//    public Page<ListingResponseDTO> getActiveListings(Pageable pageable) {
//        Page<Listing> listings = listingRepository.findByIsActiveTrueAndExpiresAtAfter(
//                LocalDateTime.now(),
//                pageable
//        );
//
//        return listings.map(listingMapper::toResponseDTO);
//    }

    @Override
    @Transactional
    public ListingResponseDTO updateListing(Long id, ListingRequestDTO dto, Long currentUserId) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found with id: " + id));

        if (!listing.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Able to update only if it is your own listings");
        }

        listingMapper.updateEntityFromDTO(dto, listing);

        if (dto.dormId() != null && !listing.getDorm().getId().equals(dto.dormId())) {
            Dorm newDorm = dormRepository.findById(dto.dormId())
                    .orElseThrow(() -> new EntityNotFoundException("Dorm not found with id: " + dto.dormId()));
            listing.setDorm(newDorm);
        }

        Listing updated = listingRepository.save(listing);
        return listingMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteListing(Long id, Long currentUserId) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found with id: " + id));

        if (!listing.getUser().getId().equals(currentUserId))
            throw new IllegalArgumentException("Able to delete only if owner.");

        listing.setIsActive(false);
        listingRepository.save(listing);
    }

//    @Override
//    public Page<ListingResponseDTO> searchListings(String keyword, Long dormId, Pageable pageable) {
//        Page<Listing> listings;
//
//        if (keyword != null && dormId != null) {
//            listings = listingRepository.searchByKeywordAndDorm(
//                    keyword.toLowerCase(),
//                    dormId,
//                    LocalDateTime.now(),
//                    pageable
//            );
//        } else if (keyword != null) {
//            listings = listingRepository.searchByKeyword(
//                    keyword.toLowerCase(),
//                    LocalDateTime.now(),
//                    pageable
//            );
//        } else if (dormId != null) {
//            listings = listingRepository.findByDormIdAndIsActiveTrueAndExpiresAtAfter(
//                    dormId,
//                    LocalDateTime.now(),
//                    pageable
//            );
//        } else {
//            listings = listingRepository.findByIsActiveTrueAndExpiresAtAfter(
//                    LocalDateTime.now(),
//                    pageable
//            );
//        }
//
//        return listings.map(listingMapper::toResponseDTO);
//    }
}
