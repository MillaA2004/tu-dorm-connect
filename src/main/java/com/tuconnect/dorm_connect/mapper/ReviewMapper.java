package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.ReviewRequestDTO;
import com.tuconnect.dorm_connect.dto.ReviewResponseDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.model.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    // RequestDTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dorm", ignore = true)
    Review toEntity(ReviewRequestDTO dto);

    // Entity → ResponseDTO
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "dormId", source = "dorm.id")
    ReviewResponseDTO toDTO(Review entity);

    @AfterMapping
    default void setRelations(ReviewRequestDTO dto, @MappingTarget Review entity) {
        if (dto.userId() != null) {
            User user = new User();
            user.setUserId(dto.userId());
            entity.setUser(user);
        }
        if (dto.dormId() != null) {
            Dorm dorm = new Dorm();
            dorm.setId(dto.dormId());
            entity.setDorm(dorm);
        }
    }
}
