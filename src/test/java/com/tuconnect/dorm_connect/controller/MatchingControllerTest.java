//package com.tuconnect.dorm_connect.controller;
//
//import com.tuconnect.dorm_connect.model.Listing;
//import com.tuconnect.dorm_connect.model.Questionnaire;
//import com.tuconnect.dorm_connect.model.Roles;
//import com.tuconnect.dorm_connect.model.User;
//import com.tuconnect.dorm_connect.repository.ListingRepository;
//import com.tuconnect.dorm_connect.repository.QuestionnaireRepository;
//import com.tuconnect.dorm_connect.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@EntityScan(basePackages = "com.tuconnect.dorm_connect.model")
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//class MatchControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private UserRepository userRepository;
//    @Autowired private ListingRepository listingRepository;
//    @Autowired private QuestionnaireRepository questionnaireRepository;
//
//    @BeforeEach
//    void setUp() {
//        listingRepository.deleteAll();
//        questionnaireRepository.deleteAll();
//        userRepository.deleteAll();
//
//        // Viewer
//        User viewer = User.builder()
//                .firstName("Alice")
//                .lastName("Viewer")
//                .email("viewer@example.com")
//                .password("password")
//                .gender(User.Gender.FEMALE)
//                .major("Computer Science")
//                .year(2)
//                .role(Roles.Users)
//                .build();
//        viewer = userRepository.save(viewer);
//
//        Questionnaire viewerQ = new Questionnaire();
//        viewerQ.setUser(viewer);
//        viewerQ.setSmokes(true);
//        viewerQ.setDrinks(false);
//        viewerQ.setPartyHome(true);
//        viewerQ.setStayAtHome(true);
//        viewerQ.setSharesCleaning(true);
//        viewerQ.setEarlyRiser(true);
//        viewerQ.setStudiesInRoom(true);
//        viewerQ.setPrefersSocialRoommate(true);
//        viewerQ.setCooksInDorm(true);
//        viewerQ.setUsesHeadphones(true);
//        viewerQ.setSharesItems(true);
//        viewerQ.setCleanliness(4);
//        viewerQ.setBedtime(2);
//        viewerQ.setNeedsQuiet(3);
//        viewerQ.setGuestFrequency(2);
//        viewerQ.setFoodSharing(3);
//        viewerQ.setEntertainmentFrequency(2);
//        viewerQ.setPersonalSpaceImportance(4);
//        viewerQ.setMbti("INTJ");
//        viewerQ.setSpecialty("Computer Science");
//        viewerQ.setAge(21);
//        questionnaireRepository.save(viewerQ);
//
//        viewer.setQuestionnaire(viewerQ);
//        userRepository.save(viewer);
//
//        // Poster
//        User poster = User.builder()
//                .firstName("Bob")
//                .lastName("Poster")
//                .email("poster@example.com")
//                .password("password")
//                .gender(User.Gender.MALE)
//                .major("Engineering")
//                .year(3)
//                .role(Roles.Users)
//                .build();
//        poster = userRepository.save(poster);
//
//        Questionnaire posterQ = new Questionnaire();
//        posterQ.setUser(poster);
//        posterQ.setSmokes(true);
//        posterQ.setDrinks(false);
//        posterQ.setPartyHome(true);
//        posterQ.setStayAtHome(true);
//        posterQ.setSharesCleaning(true);
//        posterQ.setEarlyRiser(true);
//        posterQ.setStudiesInRoom(true);
//        posterQ.setPrefersSocialRoommate(true);
//        posterQ.setCooksInDorm(true);
//        posterQ.setUsesHeadphones(true);
//        posterQ.setSharesItems(true);
//        posterQ.setCleanliness(4);
//        posterQ.setBedtime(2);
//        posterQ.setNeedsQuiet(3);
//        posterQ.setGuestFrequency(2);
//        posterQ.setFoodSharing(3);
//        posterQ.setEntertainmentFrequency(2);
//        posterQ.setPersonalSpaceImportance(4);
//        posterQ.setMbti("INTJ");
//        posterQ.setSpecialty("Computer Science");
//        posterQ.setAge(21);
//        questionnaireRepository.save(posterQ);
//
//        poster.setQuestionnaire(posterQ);
//
//        Listing listing = new Listing();
//        listing.setUser(poster);
//        listingRepository.save(listing);
//
//        poster.setListings(List.of(listing));
//        userRepository.save(poster);
//    }
//
//    @Test
//    void generateMatches_shouldReturn200AndNonEmptyJson() throws Exception {
//        mockMvc.perform(get("/matches/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].poster.firstName").value("Bob"))
//                .andExpect(jsonPath("$[0].score").isNumber());
//    }
//}