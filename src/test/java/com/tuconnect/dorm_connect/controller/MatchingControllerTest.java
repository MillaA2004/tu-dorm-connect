package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.ListingRepository;
import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@EntityScan(basePackages = "com.tuconnect.dorm_connect.model")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MatchControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ListingRepository listingRepository;
    @Autowired private QuestionnaireRepository questionnaireRepository;

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();
        questionnaireRepository.deleteAll();
        userRepository.deleteAll();

        // Viewer
        User viewer = User.builder()
                .firstName("Alice")
                .lastName("Viewer")
                .email("viewer@example.com")
                .password("password")
                .gender(User.Gender.FEMALE)
                .major("Computer Science")
                .year(2)
                .role(Roles.Users)
                .build();
        viewer = userRepository.save(viewer);

        Questionnaire viewerQ = new Questionnaire();
        viewerQ.setUser(viewer);
        questionnaireRepository.save(viewerQ);

        viewer.setQuestionnaire(viewerQ);
        userRepository.save(viewer);

        // Poster
        User poster = User.builder()
                .firstName("Bob")
                .lastName("Poster")
                .email("poster@example.com")
                .password("password")
                .gender(User.Gender.MALE)
                .major("Engineering")
                .year(3)
                .role(Roles.Users)
                .build();
        poster = userRepository.save(poster);

        Questionnaire posterQ = new Questionnaire();
        posterQ.setUser(poster);
        questionnaireRepository.save(posterQ);

        poster.setQuestionnaire(posterQ);

        Listing listing = new Listing();
        listing.setUser(poster);
        listingRepository.save(listing);

        poster.setListings(List.of(listing));
        userRepository.save(poster);
    }

    @Test
    void generateMatches_shouldReturn200() throws Exception {
        mockMvc.perform(get("/matches/1"))
                .andExpect(status().isOk());
    }
}
