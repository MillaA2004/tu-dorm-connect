package com.tuconnect.dorm_connect.mapper;

import com.tuconnect.dorm_connect.dto.Listing.ListingRequestDTO;
import com.tuconnect.dorm_connect.dto.Listing.ListingResponseDTO;
import com.tuconnect.dorm_connect.model.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ListingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dorm", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "preferencesJson", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Listing toEntity(ListingRequestDTO dto);

    List<ListingResponseDTO> toResponseDTOList(List<Listing> listings);

    @Mapping(target = "dormId", source = "dorm.id")
    @Mapping(target = "dormName", source = "dorm.name")
    @Mapping(target = "userId", source = "user.id")
    //@Mapping(target = "username", source = "user.username")
    ListingResponseDTO toResponseDTO(Listing listing);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dorm", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "preferencesJson", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDTO(ListingRequestDTO dto, @MappingTarget Listing listing);
}
