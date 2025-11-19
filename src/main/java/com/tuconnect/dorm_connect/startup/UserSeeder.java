//package com.tuconnect.dorm_connect.startup;
//
//import com.tuconnect.dorm_connect.model.Dorm;
//import com.tuconnect.dorm_connect.model.User;
//import com.tuconnect.dorm_connect.repository.DormRepository;
//import com.tuconnect.dorm_connect.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.NoSuchElementException;
//
//@Profile("dev")
//@Component
//@RequiredArgsConstructor
//@Order(1)
//public class UserSeeder implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final DormRepository dormRepository;
//
//    @Override
//    public void run(String... args) {
//        if(userRepository.count() == 0) {
//            Dorm dorm1 = dormRepository.findByName("Block 3")
//                    .orElseThrow(() -> new NoSuchElementException("Dorm not found"));
//            Dorm dorm2 = dormRepository.findByName("Block 14")
//                    .orElseThrow(() -> new NoSuchElementException("Dorm not found"));
//            User user1 = User.builder()
//                    .email("milangelova@tu-sofia.bg")
//                    .passwordHash("hashed123")
//                    .name("Milla")
//                    .facultyNumber("F12345")
//                    .interests("art, gaming")
//                    .createdAt(LocalDateTime.now().minusDays(5))
//                    .dorm(dorm2)
//                    .build();
//            User user2 = User.builder()
//                    .email("knikolov@tu-sofia.bg")
//                    .password("hashed456")
//                    .name("Kaloyan")
//                    .facultyNumber("F67890")
//                    .interests("coding, climbing")
//                    .createdAt(LocalDateTime.now().minusDays(3))
//                    .dorm(dorm1)
//                    .build();
//
//            userRepository.saveAll(List.of(user1, user2));
//            System.out.println("--- 2 Users Seeded---");
//        }
//    }
//}
