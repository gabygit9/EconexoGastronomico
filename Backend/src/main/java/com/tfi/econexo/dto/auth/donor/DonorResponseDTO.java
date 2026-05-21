package com.tfi.econexo.dto.auth.donor;

public record DonorResponseDTO(

        Long id,
        String email,
        String tradeName,
        String legalName,
        String taxId,
        String phoneNumber,
        String street,
        String streetNumber,
        Integer floor,
        String apartment,
        Long neighborhoodId
) {
}
