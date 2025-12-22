package com.tuconnect.dorm_connect.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User.Gender;
import com.tuconnect.dorm_connect.repository.ListingRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ListingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    private User poster;

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();
        questionnaireRepository.deleteAll();
        userRepository.deleteAll();

        poster = new User();
        poster.setFirstName("Milla");
        poster.setLastName("Angelova");
        poster.setEmail("test@example.com");
        poster.setPassword("password");
        poster.setAcademicYear(3);
        poster.setMajor("ISN");
        poster.setGender(Gender.FEMALE);
        poster.setRole(Roles.User);
        poster = userRepository.save(poster);

        Questionnaire q = new Questionnaire();
        q.setUser(poster);
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
        ListingRequestDTO requestDTO = new ListingRequestDTO(
                "Title",
                "Desc",
                100.0,
                "Dorm A",
                5
        );

        mockMvc.perform(post("/listings/poster/{posterId}", poster.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.posterId").value(poster.getId()));

        assertThat(listingRepository.count()).isEqualTo(1);
    }

    @Test
    void getActiveListings_shouldReturnAll_WhenGuest() throws Exception {
        createActiveListing(poster, "Guest View");

        mockMvc.perform(get("/listings/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Guest View"));
    }

    @Test
    void getActiveListings_shouldFilter_WhenUserLoggedIn() throws Exception {
        User femaleUser = createHelperUser("jane@test.com", Gender.FEMALE);
        createActiveListing(femaleUser, "Female Listing");

        User maleUser = createHelperUser("john@test.com", Gender.MALE);
        createActiveListing(maleUser, "Male Listing");

        createActiveListing(poster, "My Own Listing");

        mockMvc.perform(get("/listings/active")
                        .param("viewerId", poster.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Female Listing"));
    }

    @Test
    void searchListings_shouldFilter_WhenUserLoggedIn() throws Exception {
        User maleUser = createHelperUser("john@test.com", Gender.MALE);
        createActiveListing(maleUser, "Quiet Room (Male)");

        User femaleUser = createHelperUser("jane@test.com", Gender.FEMALE);
        createActiveListing(femaleUser, "Quiet Room (Female)"); // Compatible

        // Act: Search for "Quiet" as the Female 'poster'
        mockMvc.perform(get("/listings/search")
                        .param("keyword", "Quiet")
                        .param("viewerId", poster.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Should only find 1
                .andExpect(jsonPath("$[0].title").value("Quiet Room (Female)"));
    }

    @Test
    void deleteListing_shouldDeactivateListing() throws Exception {
        Listing listing = createActiveListing(poster, "ToDelete");

        mockMvc.perform(delete("/listings/{id}", listing.getId())
                        .param("currentUserId", poster.getId().toString()))
                .andExpect(status().isNoContent());

        Listing updated = listingRepository.findById(listing.getId()).orElseThrow();
        assertThat(updated.getIsActive()).isFalse();
    }

    // --- Helper Methods ---

    private User createHelperUser(String email, Gender gender) {
        User u = new User();
        u.setFirstName("Helper");
        u.setLastName("User");
        u.setEmail(email);
        u.setPassword("pass");
        u.setGender(gender);
        u.setAcademicYear(2);
        u.setMajor("Computer Science");
        u.setRole(Roles.User);
        return userRepository.save(u);
    }

    private Listing createActiveListing(User owner, String title) {
        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription("Description");
        listing.setPrice(100.0);
        listing.setDorm("Dorm A");
        listing.setPoster(owner);
        listing.setIsActive(true);
        listing.setCreatedAt(LocalDateTime.now());
        listing.setExpiresAt(LocalDateTime.now().plusDays(10));
        return listingRepository.save(listing);
    }
}