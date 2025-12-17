package com.tuconnect.dorm_connect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = ListingController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ListingControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ListingService listingService;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void createListing_shouldReturnCreatedListing() throws Exception {
        ListingResponseDTO responseDTO = new ListingResponseDTO(
                1L, "Title", "Desc", 100.0,
                null, "Dorm A", 1L, true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(5)
        );

        when(listingService.createListing(any(ListingRequestDTO.class))).thenReturn(responseDTO);

        ListingRequestDTO requestDTO = new ListingRequestDTO("Title", "Desc", 100.0, "Dorm A", 1L, 5);

        mockMvc.perform(post("/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void getActiveListings_shouldReturnList() throws Exception {
        ListingResponseDTO dto = new ListingResponseDTO(
                1L, "Title", "Desc", 100.0,
                null, "Dorm A", 1L, true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(5)
        );

        when(listingService.getActiveListings()).thenReturn(List.of(dto));

        mockMvc.perform(get("/listings/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void getListingById_shouldReturnListing() throws Exception {
        ListingResponseDTO dto = new ListingResponseDTO(
                1L, "Title", "Desc", 100.0,
                null, "Dorm A", 1L, true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(5)
        );

        when(listingService.getListingById(1L)).thenReturn(dto);

        mockMvc.perform(get("/listings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void deleteListing_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/listings/1")
                        .param("currentUserId", "1"))
                .andExpect(status().isNoContent());
    }
}

