package com.tuconnect.dorm_connect.startup;

import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap-admin.userid:999}")
    private Long userId;

    @Value("${app.bootstrap-admin.email:}")
    private String email;

    @Value("${app.bootstrap-admin.password:}")
    private String password;

    @Value("${app.bootstrap-admin.first-name:Admin}")
    private String firstName;

    @Value("${app.bootstrap-admin.last-name:User}")
    private String lastName;

    @Value("${app.bootstrap-admin.gender:MALE}")
    private String gender;

    @Value("${app.bootstrap-admin.major:Administration}")
    private String major;

    @Value("${app.bootstrap-admin.academic-year:1}")
    private Integer academicYear;

    @Override
    public void run(String... args) {
        if (!enabled) return;

        if (userRepository.findByRole(Roles.Admin).isPresent()) {
            return;
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("Bootstrap admin is enabled but email/password are missing.");
        }

        User admin = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .profileImageUrl(null)
                .gender(User.Gender.valueOf(gender))
                .major(major)
                .academicYear(academicYear)
                .role(Roles.Admin)
                .suspendedUntil(null)
                .deleted(false)
                .deletedAt(null)
                .build();

        userRepository.save(admin);
    }
}