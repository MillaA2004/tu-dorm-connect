package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.repository.DormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Order(0)
public class DataSeeder implements CommandLineRunner {

    private final DormRepository dormRepository;

    @Override
    public void run(String... args) throws Exception {
        seedDorms();
    }

    private void seedDorms() {
        if (dormRepository.count() == 0) {
            List<Dorm> dorms = List.of(
                    Dorm.builder()
                            .name("Block 54")
                            .description("Family dorm.")
                            .price(76.0)
                            .address("Studentski Grad, Block 54")
                            .latitude(42.6456)
                            .longitude(23.3417)
                            .build(),

                    Dorm.builder()
                            .name("Block 14")
                            .description("Renewed dorm of TU from 2015 with solar panels.")
                            .price(69.0)
                            .address("Studentski Grad, Block 14")
                            .latitude(42.6503)
                            .longitude(23.3422)
                            .build(),

                    Dorm.builder()
                            .name("Block 3")
                            .description("TU dorm for the 6.00 GPA")
                            .price(96.0)
                            .address("Studentski Grad, Block 3")
                            .latitude(42.6593)
                            .longitude(23.3503)
                            .build()
            );

            dormRepository.saveAll(dorms);
            System.out.println("✅ Database seeded with 3 Dorms.");
        }
    }
}
