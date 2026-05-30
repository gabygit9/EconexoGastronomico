package com.tfi.econexo.mappers;

import com.tfi.econexo.dto.auth.driver.VehicleRegistrationDTO;
import com.tfi.econexo.dto.auth.driver.VehicleResponseDTO;
import com.tfi.econexo.entity.logistics.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "driver", ignore = true)
    Vehicle toEntity(VehicleRegistrationDTO vehicleDTO);

    VehicleResponseDTO toResponseDTO(Vehicle vehicle);
}
