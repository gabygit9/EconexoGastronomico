package com.tfi.econexo.mappers;

import com.tfi.econexo.dtos.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.donor.DonorResponseDTO;
import com.tfi.econexo.entities.donation.Donor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonorMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "neighborhood", ignore = true)
    @Mapping(target = "location", ignore = true)
    Donor toEntity(DonorRegistrationDTO dto);

    @Mapping(source = "neighborhood.id", target = "neighborhoodId")
    DonorResponseDTO toResponseDTO(Donor donor);
}
