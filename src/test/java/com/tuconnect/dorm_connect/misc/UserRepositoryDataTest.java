package com.tuconnect.dorm_connect.misc;

import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.Questionnaire;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.model.User.Gender;
import com.tuconnect.dorm_connect.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryDataTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByQuestionnaireIsNotNullAndListingsIsNotEmpty_shouldFilterCorrectly() {
        // User with questionnaire and listing
        User poster = User.builder()
                .firstName("Poster")
                .lastName("User")
                .email("poster@test.com")
                .password("pass")
                .gender(Gender.FEMALE)
                .major("Math")
                .academicYear(1)
                .build();
        poster = entityManager.persistAndFlush(poster);

        Questionnaire q = new Questionnaire();
        q.setUser(poster);
        entityManager.persist(q);

        Listing l = new Listing();
        l.setPoster(poster);
        l.setTitle("Room");
        l.setDescription("Desc");
        l.setPrice(10.0);
        l.setDorm("Dorm");
        entityManager.persist(l);

        // User with questionnaire but NO listing
        User onlyQ = User.builder()
                .firstName("No")
                .lastName("Listing")
                .email("no@test.com")
                .password("pass")
                .gender(Gender.MALE)
                .major("Art")
                .academicYear(2)
                .build();
        onlyQ = entityManager.persistAndFlush(onlyQ);
        Questionnaire q2 = new Questionnaire();
        q2.setUser(onlyQ);
        entityManager.persist(q2);

        entityManager.flush();

        List<User> results = userRepository.findByQuestionnaireIsNotNullAndListingsIsNotEmpty();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail()).isEqualTo("poster@test.com");
    }
}