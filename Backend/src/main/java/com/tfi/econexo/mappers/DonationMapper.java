package com.tfi.econexo.mappers;

import com.tfi.econexo.dto.donation.DonationItemResponseDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(target = "businessName", source = "donor.tradeName")
    @Mapping(target = "status", expression = "java(donation.getStatus().name())")
    DonationResponseDTO toResponseDTO(Donation donation);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitOfMeasure", source = "product.unitOfMeasure.description")
    @Mapping(target = "category", source = "product.category.description")
    @Mapping(target = "productType", source = "product.productType.description")
    DonationItemResponseDTO toItemResponseDTO(DonationItem item);
}
