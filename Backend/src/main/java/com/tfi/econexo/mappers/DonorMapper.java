package com.tfi.econexo.mappers;

import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.entity.donation.Donor;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.security.UserSec;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DonorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "neighborhood", source = "neighborhood")
    @Mapping(target = "donorType", source = "dto.donorType")
    @Mapping(target = "location", expression = "java(com.tfi.econexo.utils.GeometryUtils.createPoint(dto.longitude(), dto.latitude()))")
    Donor toEntity(DonorRegistrationDTO dto, UserSec user, Neighborhood neighborhood);

    @Mapping(target = "neighborhoodId", source = "donor.neighborhood.id")
    @Mapping(target = "email", source = "donor.user.email")
    @Mapping(target = "status", source = "status")
    DonorResponseDTO toResponseDTO(Donor donor);

    default Point createPoint(Double longitude, Double latitude) {
        if(longitude == null || latitude == null){
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
