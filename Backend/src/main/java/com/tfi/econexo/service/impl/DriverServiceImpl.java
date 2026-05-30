package com.tfi.econexo.service.impl;

import com.tfi.econexo.entity.logistics.Driver;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public Optional<Driver> findByTaxId(String taxId) {
        return driverRepository.findByTaxId(taxId);
    }

    @Override
    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }
}
