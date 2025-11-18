package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.repository.ListingRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import jakarta.servlet.http.HttpServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Order(2)
public class ListingSeeder implements CommandLineRunner {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final DormRepository dormRepository;

    @Override
    public void run(String... args) {
        if (listingRepository.count() == 0) {
            User user = userRepository.findByEmail("milangelova@tu-sofia.bg")
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            Dorm dorm = dormRepository.findByName("Block 14")
                    .orElseThrow(() -> new NoSuchElementException("Dorm not found"));

            Listing listing = Listing.builder()
                    .title("Room 513 Available")
                    .description("Quiet room with good sunlight.")
                    .priceRange("67-100")
                    .preferencesJson("{\"gender\":\"female\",\"smoking\":false}")
                    .createdAt(LocalDateTime.now().minusDays(7))
                    .expiresAt(LocalDateTime.now().plusDays(14))
                    .user(user)
                    .dorm(dorm)
                    .build();

            listingRepository.save(listing);
            System.out.println("--- 1 Listing Seeded ---");
        }
    }
}
