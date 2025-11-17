package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.repository.DormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Order(0)
public class DormSeeder implements CommandLineRunner {

    private final DormRepository dormRepository;

    @Override
    public void run(String... args) {
        if (dormRepository.count() == 0) {
            dormRepository.save(new Dorm("Block 14"));
            dormRepository.save(new Dorm("Block 3"));
            System.out.println("--- 2 Dorms Seeded ---");
        }
    }
}
