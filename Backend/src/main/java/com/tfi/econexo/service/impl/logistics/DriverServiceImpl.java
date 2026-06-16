package com.tfi.econexo.service.impl.logistics;

import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.mappers.DriverMapper;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.service.logistics.DriverService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public Optional<Driver> findByTaxId(String taxId) {
        return driverRepository.findByTaxId(taxId);
    }

    @Override
    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public DriverResponseDTO getProfileByEmail(String email) {
        Driver driver = driverRepository.findByUser_Email((email))
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        return driverMapper.toResponseDTO(driver);
    }

    @Override
    public Optional<Driver> findEntityByEmail(String email) {
        return driverRepository.findByUser_Email(email);
    }
}
