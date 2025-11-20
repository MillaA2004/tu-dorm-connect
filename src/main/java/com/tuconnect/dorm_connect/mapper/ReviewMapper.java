package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.ReviewDTO;
import com.tuconnect.dorm_connect.model.Dorm;
import com.tuconnect.dorm_connect.model.Review;
import com.tuconnect.dorm_connect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dorm", ignore = true)
    Review toEntity(ReviewDTO dto);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "dormId", source = "dorm.id")
    ReviewDTO toDTO(Review review);

    @AfterMapping
    default void setRelations(ReviewDTO dto, @MappingTarget Review entity) {
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
