package com.tuconnect.dorm_connect.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ListingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName("Milla");
        user.setLastName("Angelova");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setAcademicYear(3);
        user.setMajor("ISN");
        user.setGender(User.Gender.FEMALE);
        user.setRole(Roles.User);

        savedUser = userRepository.save(user);
        Questionnaire q = new Questionnaire();
        q.setUser(savedUser);
        q.setSmokes(true);
        q.setDrinks(false);
        q.setPartyHome(true);
        q.setStayAtHome(true);
        q.setSharesCleaning(true);
        q.setEarlyRiser(true);
        q.setStudiesInRoom(true);
        q.setPrefersSocialRoommate(true);
        q.setCooksInDorm(true);
        q.setUsesHeadphones(true);
        q.setSharesItems(true);
        q.setCleanliness(4);
        q.setBedtime(2);
        q.setNeedsQuiet(3);
        q.setGuestFrequency(2);
        q.setFoodSharing(3);
        q.setEntertainmentFrequency(2);
        q.setPersonalSpaceImportance(4);
        q.setMbti("INTJ");
        q.setSpecialty("Computer Science");
        q.setAge(21);

        questionnaireRepository.save(q);
    }

    @Test
    void createAndFetchListing_shouldWorkEndToEnd() throws Exception {
        // Arrange: use the savedUser’s ID
        ListingRequestDTO requestDTO = new ListingRequestDTO(
                "Title", "Desc", 100.0, "Dorm A", savedUser.getId(), 5
        );

        // Act: POST /listings
        mockMvc.perform(post("/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"));

        // Act: GET /listings/active
        mockMvc.perform(get("/listings/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title"));
    }
}


