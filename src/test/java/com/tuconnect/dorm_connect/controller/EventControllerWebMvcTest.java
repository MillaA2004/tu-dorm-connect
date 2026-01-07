package com.tuconnect.dorm_connect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Event.EventResponseDTO;
import com.tuconnect.dorm_connect.security.JwtAuthenticationFilter;
import com.tuconnect.dorm_connect.security.JwtTokenProvider;
import com.tuconnect.dorm_connect.security.CustomUserDetailsService;
import com.tuconnect.dorm_connect.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class EventControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getAllEvents_shouldReturnList() throws Exception {
        EventResponseDTO event = new EventResponseDTO(1L, "Party", "Desc", "Addr", LocalDateTime.now().plusDays(1), 10, null, "Social", 0.0, 0.0, null, null);

        when(eventService.getAllEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Party"));

        verify(eventService).getAllEvents();
    }

    @Test
    void getEventById_shouldReturnEvent() throws Exception {
        EventResponseDTO event = new EventResponseDTO(1L, "Study Session", "Focus", "Library", LocalDateTime.now().plusDays(1), 5, null, "Study", 0.0, 0.0, null, null);

        when(eventService.getEventById(1L)).thenReturn(event);

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Study Session"));
    }

    @Test
    void joinEvent_shouldCallService() throws Exception {
        EventResponseDTO response = new EventResponseDTO(1L, "Party", "Desc", "Addr", LocalDateTime.now().plusDays(1), 10, null, "Social", 0.0, 0.0, null, null);

        when(eventService.joinEvent(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/events/1/join"))
                .andExpect(status().isOk());

        verify(eventService).joinEvent(eq(1L), any());
    }

    @Test
    void deleteEvent_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/events/10"))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(eq(10L), any());
    }

    @Test
    void searchEvents_shouldReturnResults() throws Exception {
        when(eventService.searchEvents("test")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/events/search").param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}