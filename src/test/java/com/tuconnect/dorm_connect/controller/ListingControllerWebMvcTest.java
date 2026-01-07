package com.tuconnect.dorm_connect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Dorm.DormSummaryDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.security.JwtAuthenticationFilter;
import com.tuconnect.dorm_connect.service.ListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ListingController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ListingControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListingService listingService;
    @Test
    void getDormOptions_shouldReturnList() throws Exception {
        DormSummaryDTO dorm1 = new DormSummaryDTO(1L, "Block 54");
        DormSummaryDTO dorm2 = new DormSummaryDTO(2L, "Block 14");
        when(listingService.getAllDormsForDropdown()).thenReturn(List.of(dorm1, dorm2));

        mockMvc.perform(get("/listings/dorms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].dormName").value("Block 54"))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(listingService).getAllDormsForDropdown();
    }

    @Test
    void getListingsByDormId_shouldCallService() throws Exception {
        // Arrange
        Long dormId = 101L;
        when(listingService.getListingsByDormId(dormId)).thenReturn(Collections.emptyList());

        // Act
        mockMvc.perform(get("/listings/dorm/{dormId}", dormId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        verify(listingService).getListingsByDormId(dormId);
    }

    @Test
    void createListing_shouldAcceptValidJsonWithDormId() throws Exception {
        // Arrange
        Long posterId = 1L;

        ListingRequestDTO requestDto = new ListingRequestDTO(
                "Nice Room",
                "Description here",
                100.0,
                55L,
                30
        );

        // Act
        mockMvc.perform(post("/listings/poster/{posterId}", posterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // Sends JSON
                .andExpect(status().isCreated());

        // Assert
        verify(listingService).createListing(eq(posterId), any(ListingRequestDTO.class));
    }

    @Test
    void getActiveListings_shouldCallServiceActive_WhenNoViewerId() throws Exception {
        when(listingService.getActiveListings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/listings/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(listingService).getActiveListings();
    }

    @Test
    void getActiveListings_shouldCallServiceCompatible_WhenViewerIdProvided() throws Exception {
        Long viewerId = 5L;
        when(listingService.getCompatibleListings(viewerId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/listings/active")
                        .param("viewerId", String.valueOf(viewerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(listingService).getCompatibleListings(viewerId);
    }

    @Test
    void searchListings_shouldPassViewerId_WhenProvided() throws Exception {
        String keyword = "quiet";
        Long viewerId = 10L;
        when(listingService.searchListings(eq(keyword), eq(viewerId)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/listings/search")
                        .param("keyword", keyword)
                        .param("viewerId", String.valueOf(viewerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(listingService).searchListings(keyword, viewerId);
    }
}