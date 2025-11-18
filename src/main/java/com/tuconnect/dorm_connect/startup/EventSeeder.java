package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Event;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.EventRepository;
import com.tuconnect.dorm_connect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Order(4)
public class EventSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (eventRepository.count() == 0) {
            User user1 = userRepository.findByEmail("milangelova@tu-sofia.bg")
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            User user2 = userRepository.findByEmail("knikolov@tu-sofia.bg")
                    .orElseThrow(() -> new NoSuchElementException("User not found"));

            Event event = Event.builder()
                    .title("Dorm Clean-Up Day")
                    .description("Join us to clean and decorate Block 3!")
                    .location("Block 3 Courtyard")
                    .dateTime(LocalDateTime.now().plusDays(2))
                    .capacity(30)
                    .createdAt(LocalDateTime.now())
                    .organizer(user1)
                    .participants(List.of(user1, user2))
                    .build();

            eventRepository.save(event);
            System.out.println("--- 1 Event Seeded ---");
        }
    }
}
