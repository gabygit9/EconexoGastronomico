package com.tfi.econexo.service;

import com.tfi.econexo.model.logistics.Driver;

import java.util.Optional;

public interface DriverService {

    Optional<Driver> findByTaxId(String taxId);
    Driver save(Driver driver);
}
