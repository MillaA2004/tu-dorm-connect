package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.mapper.ListingMapper;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.ListingRepository;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.implementations.ListingServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ListingServiceImplTest {

    @Mock
    private ListingRepository listingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ListingMapper listingMapper;
    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @InjectMocks
    private ListingServiceImpl listingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createListing_shouldSaveAndReturnResponse() {
        ListingRequestDTO dto = new ListingRequestDTO("Title", "Desc", 100.0, "Dorm A", 1L, 5);
        User user = new User();
        user.setId(1L);
        Questionnaire questionnaire = new Questionnaire();
        Listing listing = new Listing();
        ListingResponseDTO responseDTO = new ListingResponseDTO(
                1L,                       // id
                "Title",                  // title
                "Desc",                   // description
                100.0,                    // price
                null,                     // preferencesJson (or a JSON string if you want)
                "Dorm A",                 // dormName
                1L,                       // userId
                true,                     // isActive
                LocalDateTime.now(),      // createdAt
                LocalDateTime.now().plusDays(5) // expiresAt
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(1L)).thenReturn(questionnaire);
        when(listingMapper.toEntity(dto)).thenReturn(listing);
        when(listingRepository.save(listing)).thenReturn(listing);
        when(listingMapper.toResponseDTO(listing)).thenReturn(responseDTO);

        ListingResponseDTO result = listingService.createListing(dto.posterId(), dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(listingRepository).save(listing);
    }

    @Test
    void createListing_shouldThrowIfUserNotFound() {
        ListingRequestDTO dto = new ListingRequestDTO("Title", "Desc", 100.0, "Dorm A", 99L, 5);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> listingService.createListing(dto));
    }

    @Test
    void createListing_shouldThrowIfNoQuestionnaire() {
        ListingRequestDTO dto = new ListingRequestDTO(
                "Title",
                "Desc",
                100.0,
                "Dorm A",
                1L,
                5);
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionnaireRepository.findByUserId(1L)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> listingService.createListing(dto));
    }

    @Test
    void getListingById_shouldReturnResponse() {
        Listing listing = new Listing();
        listing.setId(1L);
        ListingResponseDTO responseDTO = new ListingResponseDTO(
                1L,                       // id
                "Title",                  // title
                "Desc",                   // description
                100.0,                    // price
                null,                     // preferencesJson (or a JSON string if you want)
                "Dorm A",                 // dormName
                1L,                       // userId
                true,                     // isActive
                LocalDateTime.now(),      // createdAt
                LocalDateTime.now().plusDays(5) // expiresAt
        );

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(listingMapper.toResponseDTO(listing)).thenReturn(responseDTO);

        ListingResponseDTO result = listingService.getListingById(1L);

        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    void updateListing_shouldThrowIfNotOwner() {
        Listing listing = new Listing();
        User owner = new User();
        owner.setId(1L);
        listing.setUser(owner);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ListingRequestDTO dto = new ListingRequestDTO("Title", "Desc", 100.0, "Dorm A", 1L, 5);

        assertThrows(IllegalArgumentException.class, () -> listingService.updateListing(1L, dto, 2L));
    }

    @Test
    void deleteListing_shouldDeactivateListingIfOwner() {
        Listing listing = new Listing();
        listing.setId(1L);
        User owner = new User();
        owner.setId(1L);
        listing.setPoster(owner);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        listingService.deleteListing(1L, 1L);

        assertThat(listing.getIsActive()).isFalse();
        verify(listingRepository).save(listing);
    }
}

