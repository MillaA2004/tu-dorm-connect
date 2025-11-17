package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Order(1)
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if(userRepository.count() == 0) {
            User user1 = User.builder()
                    .email("milangelova@tu-sofia.bg")
                    .interests("art, gaming")
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .build();
            User user2 = User.builder()
                    .email("knikolov@tu-sofia.bg")
                    .interests("coding, climbing")
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build();

            userRepository.saveAll(List.of(user1, user2));
            System.out.println("--- 2 Users Seeded---");
        }
    }
}
