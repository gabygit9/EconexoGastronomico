package com.tfi.econexo.mappers;

import com.tfi.econexo.dto.donation.DonationItemSummaryDTO;
import com.tfi.econexo.dto.donation.DonationSummaryResponseDTO;
import com.tfi.econexo.dto.donation.item.DonationItemRequestDTO;
import com.tfi.econexo.dto.donation.item.DonationItemResponseDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.logistics.DriverSummaryDTO;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.utils.GeometryUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = { GeometryUtils.class, com.tfi.econexo.model.enums.DonationStatus.class})
public interface DonationMapper {

    @Mapping(target = "businessName", source = "donor.tradeName")
    @Mapping(target = "status", expression = "java(donation.getStatus().name())")
    @Mapping(target = "items", source = "donationItems")
    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "ngoName", source = "ngo.ngoName")
    @Mapping(target = "pickupAddress", expression = "java(donation.getDonor() != null ? donation.getDonor().getStreet() + \" \" + donation.getDonor().getStreetNumber() + (donation.getDonor().getFloor() != null ? \" Piso \" + donation.getDonor().getFloor() : \"\") + (donation.getDonor().getApartment() != null ? \" Dpto \" + donation.getDonor().getApartment() : \"\") : \"\")")
    @Mapping(target = "dropOffAddress", expression = "java(donation.getNgo() != null ? donation.getNgo().getStreet() + \" \" + donation.getNgo().getStreetNumber() + (donation.getNgo().getFloor() != null ? \" Piso \" + donation.getNgo().getFloor() : \"\") + (donation.getNgo().getApartment() != null ? \" Dpto \" + donation.getNgo().getApartment() : \"\") : \"\")")
    @Mapping(target = "pickupLat", expression = "java(GeometryUtils.getLatitude(donation.getDonor() != null ? donation.getDonor().getLocation() : null))")
    @Mapping(target = "pickupLng", expression = "java(GeometryUtils.getLongitude(donation.getDonor() != null ? donation.getDonor().getLocation() : null))")
    @Mapping(target = "dropOffLat", expression = "java(GeometryUtils.getLatitude(donation.getNgo() != null ? donation.getNgo().getLocation() : null))")
    @Mapping(target = "dropOffLng", expression = "java(GeometryUtils.getLongitude(donation.getNgo() != null ? donation.getNgo().getLocation() : null))")
    @Mapping(target = "driverInfo", expression = "java(mapDriverInfo(donation))")
    DonationResponseDTO toResponseDTO(Donation donation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "donation", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "unitOfMeasure", ignore = true)
    DonationItem toItemEntity(DonationItemRequestDTO itemRequestDTO);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "category", source = "product.category.description")
    @Mapping(target = "productType", source = "product.productType.description")
    @Mapping(source = "unitOfMeasure.description", target = "unitOfMeasure")
    DonationItemResponseDTO toItemResponseDTO(DonationItem item);

    @Mapping(target = "businessName", source = "donor.tradeName")
    @Mapping(target = "items", source = "donationItems")
    @Mapping(target = "expirationDate", expression = "java(donation.getMinExpirationDate())")
    @Mapping(target = "requiresRefrigeration", expression = "java(donation.isAnyItemRefrigerated())")
    DonationSummaryResponseDTO toSummaryResponseDTO(Donation donation);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitOfMeasure", source = "unitOfMeasure.description")
    DonationItemSummaryDTO toItemSummaryDTO(DonationItem item);

    default DriverSummaryDTO mapDriverInfo(Donation donation){
        if(donation.getStatus() == DonationStatus.AVAILABLE ||
                donation.getStatus() == DonationStatus.REQUESTED ||
                donation.getDriver() == null){
            return null;
        }

        var driver = donation.getDriver();
        var vehicleList = driver.getVehicles();
        var vehicle = (vehicleList != null && !vehicleList.isEmpty()) ? vehicleList.get(0) : null;

        return new DriverSummaryDTO(
                driver.getFirstName(),
                driver.getLastName(),
                vehicle != null && vehicle.getNumberPlate() != null ? vehicle.getNumberPlate() : "Without number plate",
                vehicle != null && vehicle.getVehicleType() != null ? vehicle.getVehicleType().name() : "Don't specified"
        );
    }
}
