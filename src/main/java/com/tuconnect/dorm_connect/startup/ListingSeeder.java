package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.User;
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

    @Override
    public void run(String... args) {
        if (listingRepository.count() == 0) {
            User user = userRepository.findByEmail("milangelova@tu-sofia.bg")
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            Listing listing = Listing.builder()
                    .description("Room 513, Block 3")
                    .createdAt(LocalDateTime.now().minusDays(7))
                    .user(user)
                    .build();

            listingRepository.save(listing);
            System.out.println("--- 1 Listing Seeded ---");
        }
    }
}
