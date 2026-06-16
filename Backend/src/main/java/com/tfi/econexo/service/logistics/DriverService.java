package com.tfi.econexo.service.logistics;

import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.model.logistics.Driver;

import java.util.Optional;

public interface DriverService {

    Optional<Driver> findByTaxId(String taxId);
    Driver save(Driver driver);
    DriverResponseDTO getProfileByEmail(String email);
    Optional<Driver> findEntityByEmail(String email);
}
