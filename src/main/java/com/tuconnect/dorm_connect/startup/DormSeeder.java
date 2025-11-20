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
@RequiredArgsConstructor
@Order(0)
public class DormSeeder implements CommandLineRunner {

    private final DormRepository dormRepository;

    @Override
    public void run(String... args) {
        if (dormRepository.count() == 0) {
            Dorm dorm1 = Dorm.builder()
                    .name("Block 3")
                    .address("Студентски Комплекс, ж.к. Студентски град 3, 1700 София")
                    .blockNumber("3")
                    .price(88.90)
                    .amenitiesJson("{\"laundry\":true,\"gym\":false}")
                    .build();

            Dorm dorm2 = Dorm.builder()
                    .name("Block 14")
                    .address("Студентски Комплекс, 1700 София")
                    .blockNumber("14")
                    .price(67.99)
                    .amenitiesJson("{\"laundry\":true,\"gym\":true}")
                    .build();
            dormRepository.saveAll(List.of(dorm1, dorm2));
            System.out.println("--- 2 Dorms Seeded ---");
        }
    }
}
