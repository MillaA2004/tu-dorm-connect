package com.tuconnect.dorm_connect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.security.JwtAuthenticationFilter;
import com.tuconnect.dorm_connect.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;


@WebMvcTest(controllers = ListingController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListingService listingService;

    @MockBean
    private UserRepository userRepository;

    private ListingRequestDTO listingRequestDTO;
    private ListingResponseDTO listingResponseDTO;
    private List<ListingResponseDTO> listingList;

    @BeforeEach
    void setUp() {
        listingRequestDTO = new ListingRequestDTO(
                "Test Listing",              // title
                "Test Description",          // description
                100.0,                       // price
                "TestDorm",                  // dorm
                1L,                          // posterId
                30                           // expiryDays
        );

        listingResponseDTO = new ListingResponseDTO(
                1L,                          // id
                "Test Listing",              // title
                "Test Description",          // description
                100.0,                       // price
                null,                        // preferencesJson
                "TestDorm",                  // dorm
                1L,                          // posterId
                true,                        // isActive
                LocalDateTime.now(),        // createdAt
                LocalDateTime.now().plusDays(30) // expiresAt
        );

        listingList = List.of(listingResponseDTO);
    }


    @Test
    void createListing_ShouldReturnCreatedListing() throws Exception {
        Long posterId = 1L;

        when(listingService.createListing(eq(posterId), any(ListingRequestDTO.class)))
                .thenReturn(listingResponseDTO);


        mockMvc.perform(post("/listings/poster/{posterId}", posterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listingRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(listingService, times(1)).createListing(eq(posterId), any(ListingRequestDTO.class));
    }

    @Test
    void getActiveListings_ShouldReturnListOfActiveListings() throws Exception {
        // Given
        when(listingService.getActiveListings()).thenReturn(listingList);

        // When & Then
        mockMvc.perform(get("/listings/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(listingService, times(1)).getActiveListings();
    }

    @Test
    void getListingById_ShouldReturnListing() throws Exception {
        // Given
        Long listingId = 1L;
        when(listingService.getListingById(listingId)).thenReturn(listingResponseDTO);

        // When & Then
        mockMvc.perform(get("/listings/{id}", listingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(listingService, times(1)).getListingById(listingId);
    }

    @Test
    void getListingsByUserId_ShouldReturnUserListings() throws Exception {
        // Given
        Long userId = 1L;
        when(listingService.getListingsByUserId(userId)).thenReturn(listingList);

        // When & Then
        mockMvc.perform(get("/listings/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(listingService, times(1)).getListingsByUserId(userId);
    }

    @Test
    void getListingsByDorm_ShouldReturnDormListings() throws Exception {
        String dormName = "TestDorm";
        when(listingService.getListingsByDorm(dormName)).thenReturn(listingList);

        mockMvc.perform(get("/listings/dorm/{dormName}", dormName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(listingService, times(1)).getListingsByDorm(dormName);
    }

    @Test
    void updateListing_ShouldReturnUpdatedListing() throws Exception {
        // Given
        Long listingId = 1L;
        Long currentUserId = 1L;
        when(listingService.updateListing(eq(listingId), Mockito.any(ListingRequestDTO.class), eq(currentUserId)))
                .thenReturn(listingResponseDTO);

        mockMvc.perform(put("/listings/{id}", listingId)
                        .param("currentUserId", currentUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listingRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(listingService, times(1))
                .updateListing(eq(listingId), any(ListingRequestDTO.class), eq(currentUserId));
    }

    @Test
    void deleteListing_ShouldReturnNoContent() throws Exception {
        Long listingId = 1L;
        Long currentUserId = 1L;
        doNothing().when(listingService).deleteListing(listingId, currentUserId);

        mockMvc.perform(delete("/listings/{id}", listingId)
                        .param("currentUserId", currentUserId.toString()))
                .andExpect(status().isNoContent());

        verify(listingService, times(1)).deleteListing(listingId, currentUserId);
    }

    @Test
    void searchListings_WithKeyword_ShouldReturnMatchingListings() throws Exception {
        String keyword = "test";
        when(listingService.searchListings(keyword)).thenReturn(listingList);

        mockMvc.perform(get("/listings/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(listingService, times(1)).searchListings(keyword);
    }

    @Test
    void searchListings_WithoutKeyword_ShouldReturnAllListings() throws Exception {
        when(listingService.searchListings(null)).thenReturn(listingList);

        mockMvc.perform(get("/listings/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(listingService, times(1)).searchListings(null);
    }

    @Test
    void getListingsByPriceMax_ShouldReturnListingsUnderMaxPrice() throws Exception {
        Double maxPrice = 100.0;
        when(listingService.getListingsByPriceMax(maxPrice)).thenReturn(listingList);

        mockMvc.perform(get("/listings/price/max")
                        .param("maxPrice", maxPrice.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(listingService, times(1)).getListingsByPriceMax(maxPrice);
    }
}