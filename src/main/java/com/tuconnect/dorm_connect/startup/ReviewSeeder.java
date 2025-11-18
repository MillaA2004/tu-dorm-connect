package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.DormRepository;
import com.tuconnect.dorm_connect.repository.ReviewRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Order(3)
public class ReviewSeeder implements CommandLineRunner {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final DormRepository dormRepository;

    @Override
    public void run(String... args) {
        if (reviewRepository.count() == 0) {
            User user = userRepository.findByEmail("knikolov@tu-sofia.bg")
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            Dorm dorm = dormRepository.findByName("Block 14")
                    .orElseThrow(() -> new NoSuchElementException("Dorm not found"));

            Review review = Review.builder()
                    .rating(5)
                    .comment("Quiet and clean, great for studying.")
                    .categoryScoresJson("{\"cleanliness\":5,\"noise\":2,\"staff\":4}")
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .user(user)
                    .dorm(dorm)
                    .build();

            reviewRepository.save(review);
            System.out.println("--- 1 Review Seeded ---");
        }
    }
}
