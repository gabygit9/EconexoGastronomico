package com.tfi.econexo.mappers;

import com.tfi.econexo.dto.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationResponseDTO;
import com.tfi.econexo.entity.ngo.Ngo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrganizationMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "neighborhood", ignore = true)
    @Mapping(target = "location", ignore = true)
    Ngo toEntity(OrganizationRegistrationDTO organizationDTO);

    @Mapping(source = "neighborhood.id", target = "neighborhoodId")
    OrganizationResponseDTO toResponseDTO(Ngo ngo);
}
