package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.logistics.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.dto.auth.ngo.NgoRegistrationDTO;
import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.donation.Donor;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.logistics.Vehicle;
import com.tfi.econexo.model.ngo.Ngo;
import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.mappers.*;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.service.DriverService;
import com.tfi.econexo.service.NeighborhoodService;
import com.tfi.econexo.service.NgoService;
import com.tfi.econexo.service.auth.AuthService;
import com.tfi.econexo.service.auth.RoleService;
import com.tfi.econexo.service.auth.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DonorService donorService;
    private final UserService userService;
    private final RoleService roleService;
    private final NgoService ngoService;
    private final DriverService driverService;

    private final DonorMapper donorMapper;
    private final UserMapper userMapper;
    private final NgoMapper ngoMapper;
    private final DriverMapper driverMapper;
    private final VehicleMapper vehicleMapper;

    private final NeighborhoodService neighborhoodService;

    @Transactional
    @Override
    public DonorResponseDTO registerDonor(DonorRegistrationDTO donorDTO) {

        if(donorDTO == null){
            throw new IllegalArgumentException("Donor registration request cannot be null");
        }

        if (userService.findByEmail(donorDTO.email()).isPresent() || donorService.findByTaxId(donorDTO.taxId())) {
            throw new ConflictException("Donor already exists");
        }

        Role role = roleService.findByName("DONOR").orElseThrow(() -> new EntityNotFoundException("Role DONOR not found"));

        String password = userService.encryptPassword(donorDTO.password());

        UserSec user = userMapper.toEntity(donorDTO.email(), password, role);
        user = userService.save(user);

        Neighborhood neighborhood = neighborhoodService.findById(donorDTO.neighborhoodId())
                .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found"));

        Donor donor = donorMapper.toEntity(donorDTO, user, neighborhood);
        donor.setStatus(RegistrationStatus.APPROVED);

        donorService.save(donor);

        return donorMapper.toResponseDTO(donor);
    }

    @Transactional
    @Override
    public NgoResponseDTO registerNgo(NgoRegistrationDTO ngoDTO) {

        if(ngoDTO == null){
            throw new IllegalArgumentException("Ngo registration request cannot be null");
        }

        if(userService.findByEmail(ngoDTO.email()).isPresent() ||
                ngoService.findByTaxId(ngoDTO.taxId()).isPresent() ||
                ngoService.findByLegalPersonalityNumber(ngoDTO.legalPersonalityNumber()).isPresent()){
            throw new ConflictException("Ngo already exists.");
        }

        Optional<Neighborhood> neighborhood = existsNeighborhood(ngoDTO.neighborhoodId());

        Role role = roleService.findByName("NGO").orElseThrow(() -> new EntityNotFoundException("Role NGO not found"));

        String password = userService.encryptPassword(ngoDTO.password());

        UserSec user = userMapper.toEntity(ngoDTO.email(), password, role);
        user = userService.save(user);

        Ngo ngo = ngoMapper.toEntity(ngoDTO, user, neighborhood.orElse(null));
        ngo.setStatus(RegistrationStatus.PENDING);

        ngoService.save(ngo);

        return ngoMapper.toResponseDTO(ngo);
    }

    @Transactional
    @Override
    public DriverResponseDTO registerDriver(DriverRegistrationDTO driverDTO) {

        if(driverDTO == null){ throw new IllegalArgumentException("Driver registration request cannot be null");}
        int age = Period.between(driverDTO.birthDate(), LocalDate.now()).getYears();
        if(age < 18){throw new IllegalArgumentException("Driver must be at least 18 years old");}


        if(userService.findByEmail(driverDTO.email()).isPresent() || driverService.findByTaxId(driverDTO.taxId()).isPresent()){
            throw new ConflictException("Driver already exists.");
        }

        Optional<Neighborhood> neighborhood = existsNeighborhood(driverDTO.neighborhoodId());

        Role role = roleService.findByName("DRIVER").orElseThrow(() -> new EntityNotFoundException("Role DRIVER not found"));

        String password = userService.encryptPassword(driverDTO.password());

        UserSec user = userMapper.toEntity(driverDTO.email(), password, role);
        user = userService.save(user);

        Driver driver = driverMapper.toEntity(driverDTO, user, neighborhood.orElse(null));
        driver.setStatus(RegistrationStatus.PENDING);

        Vehicle vehicle = vehicleMapper.toEntity(driverDTO.vehicle());
        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);

        driverService.save(driver);

        return driverMapper.toResponseDTO(driver);
    }

    private Optional<Neighborhood> existsNeighborhood(Long neighborhoodId) {
        return Optional.ofNullable(neighborhoodService.findById(neighborhoodId).orElseThrow(() -> new EntityNotFoundException("Neighborhood not found")));
    }
}
