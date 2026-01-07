package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.mapper.ListingMapper;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.ListingRepository;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.ListingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;
    private final QuestionnaireRepository questionnaireRepository;

    @Override
    @Transactional
    public ListingResponseDTO createListing(Long posterId, ListingRequestDTO dto) {
        User poster = userRepository.findById(posterId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Listing> existingListings = listingRepository.findByPosterIdAndIsActiveTrueAndExpiresAtAfter(
                posterId,
                LocalDateTime.now()
        );

        if (!existingListings.isEmpty()) {
            throw new IllegalStateException("You already have an active listing. Please wait for it to expire or delete it before creating a new one.");
        }

        Questionnaire questionnaire = questionnaireRepository.findByUserId(posterId);
        if (questionnaire == null) {
            throw new IllegalStateException("User must complete questionnaire before posting a listing.");
        }

        Listing listing = listingMapper.toEntity(dto);
        listing.setPoster(poster);
        listing.setIsActive(true);
        if (dto.expiryDays() != null) {
            listing.setExpiresAt(LocalDateTime.now().plusDays(dto.expiryDays()));
        }

        Listing saved = listingRepository.save(listing);
        return listingMapper.toResponseDTO(saved);
    }

    @Override
    public ListingResponseDTO getListingById(Long id){
        Listing listing = listingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Listing not found."));
        return listingMapper.toResponseDTO(listing);
    }

    @Override
    public List<ListingResponseDTO> getActiveListings() {
        List<Listing> listings = listingRepository.findByIsActiveTrueAndExpiresAtAfter(LocalDateTime.now());
        return listingMapper.toResponseDTOList(listings);
    }

    @Override
    public List<ListingResponseDTO> getCompatibleListings(Long viewerId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Listing> listings = listingRepository.findByIsActiveTrueAndExpiresAtAfterAndPoster_Gender(
                LocalDateTime.now(),
                viewer.getGender()
        );
        List<Listing> filtered = listings.stream()
                .filter(l -> !l.getPoster().getId().equals(viewerId))
                .toList();

        return listingMapper.toResponseDTOList(filtered);
    }

    @Override
    public List<ListingResponseDTO> getListingsByUserId(Long userId){
        List<Listing> listings = listingRepository.findByPosterIdAndIsActiveTrueAndExpiresAtAfter(userId, LocalDateTime.now());
        return listingMapper.toResponseDTOList(listings);
    }

    @Override
    public List<ListingResponseDTO> getListingsByDorm(String dormName){
        List<Listing> listings = listingRepository
                .findByDormAndIsActiveTrueAndExpiresAtAfter(dormName, LocalDateTime.now());
        return listingMapper.toResponseDTOList(listings);
    }

    @Override
    @Transactional
    public ListingResponseDTO updateListing(Long id, ListingRequestDTO dto, Long currentPosterId) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found with id: " + id));

        if (!listing.getPoster().getId().equals(currentPosterId)) {
            throw new IllegalArgumentException("Able to update only if it is your own listings");
        }

        listingMapper.updateEntityFromDTO(dto, listing);

        Listing updated = listingRepository.save(listing);
        return listingMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteListing(Long id, Long currentPosterId) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found with id: " + id));

        if (!listing.getPoster().getId().equals(currentPosterId))
            throw new IllegalArgumentException("Able to delete only if owner.");

        listing.setIsActive(false);
        listingRepository.save(listing);
    }

    @Override
    @Transactional
    public List<ListingResponseDTO> searchListings(String keyword, Long viewerId) {
        List<Listing> listings;

           if (keyword != null) {
                listings = listingRepository.searchByKeyword(
                        keyword.toLowerCase(),
                        LocalDateTime.now()
                );
            } else {
                listings = listingRepository.findByIsActiveTrueAndExpiresAtAfter(LocalDateTime.now());
            }

            if (viewerId != null) {
                User viewer = userRepository.findById(viewerId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));

                listings = listings.stream()
                        .filter(l -> l.getPoster().getGender() == viewer.getGender())
                        .filter(l -> !l.getPoster().getId().equals(viewerId))
                        .toList();
            }

           return listingMapper.toResponseDTOList(listings);
    }

    @Override
    public List<ListingResponseDTO> getListingsByPriceMax(Double maxPrice) {
        List<Listing> listings = listingRepository .findByIsActiveTrueAndExpiresAtAfterAndPriceLessThanEqual(LocalDateTime.now(), maxPrice);
        return listingMapper.toResponseDTOList(listings);
    }
}