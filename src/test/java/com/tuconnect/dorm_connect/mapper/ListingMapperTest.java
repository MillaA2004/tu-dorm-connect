package com.tuconnect.dorm_connect.mapper;
import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Listing;
import com.tuconnect.dorm_connect.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ListingMapperTest {

    @Autowired
    private ListingMapper listingMapper;

    @Test
    void toEntity_shouldMapDormIdToDormObject() {
        // Given
        ListingRequestDTO requestDTO = new ListingRequestDTO(
                "Test Title",
                "Description",
                100.0,
                55L, // dormId
                30
        );

        // When
        Listing entity = listingMapper.toEntity(requestDTO);

        // Then
        assertThat(entity.getTitle()).isEqualTo("Test Title");
        assertThat(entity.getPrice()).isEqualTo(100.0);

        // Critical Check: Does it create a Dorm object with the ID?
        assertThat(entity.getDorm()).isNotNull();
        assertThat(entity.getDorm().getId()).isEqualTo(55L);

        // Check Constant
        assertThat(entity.getIsActive()).isTrue();
    }

    @Test
    void toResponseDTO_shouldMapDormNameCorrectly() {
        // Given
        Dorm dorm = new Dorm();
        dorm.setId(10L);
        dorm.setName("Block 3");

        User poster = new User();
        poster.setId(1L);
        poster.setFirstName("Alex");

        Listing listing = new Listing();
        listing.setId(100L);
        listing.setTitle("Nice Room");
        listing.setDorm(dorm);
        listing.setPoster(poster);

        // When
        ListingResponseDTO responseDTO = listingMapper.toResponseDTO(listing);

        // Then
        assertThat(responseDTO.title()).isEqualTo("Nice Room");

        assertThat(responseDTO.dorm()).isNotNull();
        assertThat(responseDTO.dorm().id()).isEqualTo(10L);
        assertThat(responseDTO.dorm().dormName()).isEqualTo("Block 3");
    }

    @Test
    void updateEntityFromDTO_shouldUpdateFieldsAndDormId() {
        Dorm oldDorm = new Dorm();
        oldDorm.setId(1L);

        Listing listing = new Listing();
        listing.setTitle("Old Title");
        listing.setPrice(50.0);
        listing.setDorm(oldDorm);

        ListingRequestDTO updateDTO = new ListingRequestDTO(
                "New Title",
                "New Desc",
                120.0,
                99L, // New Dorm ID
                null
        );

        // When
        listingMapper.updateEntityFromDTO(updateDTO, listing);

        // Then
        assertThat(listing.getTitle()).isEqualTo("New Title");
        assertThat(listing.getPrice()).isEqualTo(120.0);

        assertThat(listing.getDorm().getId()).isEqualTo(99L);
    }
}
