package com.tfi.econexo.mappers;

import com.tfi.econexo.dtos.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dtos.auth.driver.DriverResponseDTO;
import com.tfi.econexo.entities.logistics.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DriverMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "neighborhood", ignore = true)
    @Mapping(target = "currentLocation", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    Driver toEntity(DriverRegistrationDTO dto);

    @Mapping(source = "neighborhood.id", target = "neighborhoodId")
    DriverResponseDTO toResponseDTO(Driver driver);
}
