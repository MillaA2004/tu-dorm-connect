package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.repository.UserRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ListingController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListingService listingService;

    @MockBean
    private UserRepository userRepository;


    @Test
    void getActiveListings_shouldCallServiceActive_WhenNoViewerId() throws Exception {
        // Arrange
        when(listingService.getActiveListings()).thenReturn(Collections.emptyList());

        // Act: Request without viewerId param
        mockMvc.perform(get("/listings/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert: Service's guest method is called
        verify(listingService).getActiveListings();
    }

    @Test
    void getActiveListings_shouldCallServiceCompatible_WhenViewerIdProvided() throws Exception {
        // Arrange
        Long viewerId = 5L;
        when(listingService.getCompatibleListings(viewerId)).thenReturn(Collections.emptyList());

        // Act: Request WITH viewerId param
        mockMvc.perform(get("/listings/active")
                        .param("viewerId", String.valueOf(viewerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert: Service's secure method is called
        verify(listingService).getCompatibleListings(viewerId);
    }

    // --- Search Endpoint Tests ---

    @Test
    void searchListings_shouldPassViewerId_WhenProvided() throws Exception {
        // Arrange
        String keyword = "quiet";
        Long viewerId = 10L;
        // Mock service accepting (keyword, viewerId)
        when(listingService.searchListings(eq(keyword), eq(viewerId)))
                .thenReturn(Collections.emptyList());

        // Act
        mockMvc.perform(get("/listings/search")
                        .param("keyword", keyword)
                        .param("viewerId", String.valueOf(viewerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert: Ensure viewerId was passed to service for filtering
        verify(listingService).searchListings(keyword, viewerId);
    }

    @Test
    void searchListings_shouldPassNull_WhenNoViewerId() throws Exception {
        // Arrange
        String keyword = "quiet";
        // Mock service accepting (keyword, null)
        when(listingService.searchListings(eq(keyword), eq(null)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/listings/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(listingService).searchListings(keyword, null);
    }
}