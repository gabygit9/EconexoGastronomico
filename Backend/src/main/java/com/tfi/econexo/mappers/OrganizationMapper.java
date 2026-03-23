package com.tfi.econexo.mappers;

import com.tfi.econexo.dtos.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dtos.auth.organization.OrganizationResponseDTO;
import com.tfi.econexo.entities.organization.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrganizationMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "neighborhood", ignore = true)
    @Mapping(target = "location", ignore = true)
    Organization toEntity(OrganizationRegistrationDTO organizationDTO);

    @Mapping(source = "neighborhood.id", target = "neighborhoodId")
    OrganizationResponseDTO toResponseDTO(Organization organization);
}
