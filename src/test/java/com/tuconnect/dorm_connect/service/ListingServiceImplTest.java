package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.mapper.ListingMapper;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.User.Gender;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void createListing_shouldSaveAndReturnResponse_WhenNoActiveListingExists() {
        Long posterId = 1L;
        ListingRequestDTO dto = new ListingRequestDTO(
                "Title",
                "Desc",
                100.0,
                "Dorm A",
                5);

        User user = new User();
        user.setId(posterId);

        Questionnaire questionnaire = new Questionnaire();
        Listing listing = new Listing();
        ListingResponseDTO responseDTO = new ListingResponseDTO(
                1L,
                "Title",
                "Desc",
                100.0,
                null,
                "Dorm A",
                posterId,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)
        );

        when(userRepository.findById(posterId)).thenReturn(Optional.of(user));
        when(listingRepository.findByPosterIdAndIsActiveTrueAndExpiresAtAfter(eq(posterId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(questionnaireRepository.findByUserId(posterId)).thenReturn(questionnaire);
        when(listingMapper.toEntity(dto)).thenReturn(listing);
        when(listingRepository.save(listing)).thenReturn(listing);
        when(listingMapper.toResponseDTO(listing)).thenReturn(responseDTO);

        ListingResponseDTO result = listingService.createListing(posterId, dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(listingRepository).save(listing);
    }

    @Test
    void createListing_shouldThrow_WhenActiveListingExists() {
        Long posterId = 1L;
        ListingRequestDTO dto = new ListingRequestDTO(
                "Title",
                "Desc",
                100.0,
                "Dorm A",
                5);
        User user = new User();
        user.setId(posterId);

        when(userRepository.findById(posterId)).thenReturn(Optional.of(user));
        when(listingRepository.findByPosterIdAndIsActiveTrueAndExpiresAtAfter(eq(posterId), any(LocalDateTime.class)))
                .thenReturn(List.of(new Listing()));

        assertThrows(IllegalStateException.class, () -> listingService.createListing(posterId, dto));
        verify(listingRepository, never()).save(any());
    }

    @Test
    void createListing_shouldThrowIfUserNotFound() {
        ListingRequestDTO dto = new ListingRequestDTO("Title", "Desc", 100.0, "Dorm A", 5);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> listingService.createListing(99L, dto));
    }

    @Test
    void createListing_shouldThrowIfNoQuestionnaire() {
        Long posterId = 1L;
        ListingRequestDTO dto = new ListingRequestDTO("Title", "Desc", 100.0, "Dorm A", 5);
        User user = new User();
        user.setId(posterId);

        when(userRepository.findById(posterId)).thenReturn(Optional.of(user));
        when(listingRepository.findByPosterIdAndIsActiveTrueAndExpiresAtAfter(eq(posterId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(questionnaireRepository.findByUserId(posterId)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> listingService.createListing(posterId, dto));
    }

    @Test
    void getActiveListings_shouldReturnAllActive_ForGuest() {
        Listing listing = new Listing();
        ListingResponseDTO responseDTO = new ListingResponseDTO(
                1L,
                "T",
                "D",
                100.0,
                null,
                "A",
                1L,
                true,
                null,
                null);

        when(listingRepository.findByIsActiveTrueAndExpiresAtAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(listing));
        when(listingMapper.toResponseDTOList(List.of(listing))).thenReturn(List.of(responseDTO));

        List<ListingResponseDTO> result = listingService.getActiveListings();

        assertThat(result).hasSize(1);
        verify(listingRepository).findByIsActiveTrueAndExpiresAtAfter(any(LocalDateTime.class));
    }

    @Test
    void getCompatibleListings_shouldFilterByGenderAndExcludeOwn() {
        Long viewerId = 100L;
        User viewer = new User();
        viewer.setId(viewerId);
        viewer.setGender(Gender.MALE);

        User otherUser = new User();
        otherUser.setId(200L);
        Listing validListing = new Listing();
        validListing.setId(1L);
        validListing.setPoster(otherUser);

        Listing ownListing = new Listing();
        ownListing.setId(2L);
        ownListing.setPoster(viewer);

        when(userRepository.findById(viewerId)).thenReturn(Optional.of(viewer));

        when(listingRepository.findByIsActiveTrueAndExpiresAtAfterAndPoster_Gender(any(LocalDateTime.class), eq(Gender.MALE)))
                .thenReturn(List.of(validListing, ownListing));

        ListingResponseDTO responseDTO = new ListingResponseDTO(
                1L,
                "T",
                "D",
                100.0,
                null,
                "A",
                200L,
                true,
                null,
                null
        );

        when(listingMapper.toResponseDTOList(List.of(validListing))).thenReturn(List.of(responseDTO));

        List<ListingResponseDTO> result = listingService.getCompatibleListings(viewerId);

        assertThat(result).hasSize(1);
        verify(listingMapper).toResponseDTOList(List.of(validListing));
    }

    @Test
    void searchListings_shouldSearchAll_ForGuest() {
        String keyword = "quiet";
        Listing listing = new Listing();
        when(listingRepository.searchByKeyword(eq("quiet"), any(LocalDateTime.class)))
                .thenReturn(List.of(listing));
        when(listingMapper.toResponseDTOList(any())).thenReturn(List.of(new ListingResponseDTO(1L, "T", "D", 10.0, null, "D", 1L, true, null, null)));

        listingService.searchListings(keyword, null);

        verify(listingRepository).searchByKeyword(eq("quiet"), any(LocalDateTime.class));
    }

    @Test
    void searchListings_shouldFilterResults_ForUser() {
        Long viewerId = 1L;
        String keyword = "quiet";
        User viewer = new User();
        viewer.setId(viewerId);
        viewer.setGender(Gender.FEMALE);

        User maleUser = new User();
        maleUser.setGender(Gender.MALE);
        Listing wrongGenderListing = new Listing();
        wrongGenderListing.setPoster(maleUser);

        User femaleUser = new User();
        femaleUser.setId(2L);
        femaleUser.setGender(Gender.FEMALE);
        Listing correctListing = new Listing();
        correctListing.setPoster(femaleUser);

        when(userRepository.findById(viewerId)).thenReturn(Optional.of(viewer));
        when(listingRepository.searchByKeyword(eq("quiet"), any(LocalDateTime.class)))
                .thenReturn(List.of(wrongGenderListing, correctListing));

        listingService.searchListings(keyword, viewerId);

        verify(listingMapper).toResponseDTOList(List.of(correctListing));
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