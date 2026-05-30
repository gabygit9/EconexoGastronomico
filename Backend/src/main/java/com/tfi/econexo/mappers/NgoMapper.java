package com.tfi.econexo.mappers;

import com.tfi.econexo.dto.auth.ngo.NgoRegistrationDTO;
import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.ngo.Ngo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NgoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "neighborhood", source = "neighborhood")
    @Mapping(target = "ngoName", source = "dto.ngoName")
    @Mapping(target = "taxId", source = "dto.taxId")
    @Mapping(target = "legalPersonalityNumber", source = "dto.legalPersonalityNumber")
    @Mapping(target = "responsibleName", source = "dto.responsibleName")
    @Mapping(target = "street", source = "dto.street")
    @Mapping(target = "streetNumber", source = "dto.streetNumber")
    @Mapping(target = "floor", source = "dto.floor")
    @Mapping(target = "apartment", source = "dto.apartment")
    @Mapping(target = "phoneNumber", source = "dto.phoneNumber")
    @Mapping(target = "ngoType", expression = "java(com.tfi.econexo.entity.ngo.NgoType.valueOf(dto.ngoType().toUpperCase()))")
    @Mapping(target = "location", expression = "java(com.tfi.econexo.utils.GeometryUtils.createPoint(dto.longitude(), dto.latitude()))")
    Ngo toEntity(NgoRegistrationDTO dto, UserSec user, Neighborhood neighborhood);

    @Mapping(target = "email", source = "ngo.user.email")
    @Mapping(target = "neighborhoodId", source = "ngo.neighborhood.id")
    @Mapping(target = "status", source = "status")
    NgoResponseDTO toResponseDTO(Ngo ngo);

}
