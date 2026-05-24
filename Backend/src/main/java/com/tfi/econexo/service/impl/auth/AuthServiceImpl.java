package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dto.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.driver.DriverResponseDTO;
import com.tfi.econexo.dto.auth.login.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.login.AuthResponseDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationRegistrationDTO;
import com.tfi.econexo.dto.auth.organization.OrganizationResponseDTO;
import com.tfi.econexo.entity.donation.Donor;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.mappers.DonorMapper;
import com.tfi.econexo.mappers.DriverMapper;
import com.tfi.econexo.mappers.OrganizationMapper;
import com.tfi.econexo.repository.location.NeighborhoodRepository;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.repository.organization.OrganizationRepository;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.service.auth.AuthService;
import com.tfi.econexo.service.auth.RoleService;
import com.tfi.econexo.service.auth.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DonorService donorService;
    private final UserService userService;
    private final RoleService roleService;
    private final NeighborhoodRepository neighborhoodRepository;
    private final DonorMapper donorMapper;

    private final DriverRepository driverRepository;
    private final OrganizationRepository organizationRepository;
    private final DriverMapper driverMapper;
    private final OrganizationMapper organizationMapper;

    @Transactional
    @Override
    public DonorResponseDTO registerDonor(DonorRegistrationDTO donorDTO) {if(donorDTO == null){
        throw new IllegalArgumentException("Donor registration request cannot be null");
    }

        if (donorService.findByEmail(donorDTO.email()) || donorService.findByTaxId(donorDTO.taxId())) {
            throw new ConflictException("Donor already exists");
        }

        Role role = roleService.findByName("DONOR").orElseThrow(() -> new EntityNotFoundException("Role DONOR not found"));

        String password = userService.encryptPassword(donorDTO.password());

        //TODO UserMapper
        UserSec user = new UserSec();
        user.setEmail(donorDTO.email());
        user.setPassword(password);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialNonExpired(true);
        user.setRolesList(Set.of(role));

        Neighborhood neighborhood = neighborhoodRepository.findById(donorDTO.neighborhoodId())
                .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found"));

        Donor donor = donorMapper.toEntity(donorDTO, user, neighborhood);

        donorService.save(donor);

        return donorMapper.toResponseDTO(donor);
    }

    @Transactional
    @Override
    public DriverResponseDTO registerDriver(DriverRegistrationDTO driverRegistrationDTO) {
//        if(driverRegistrationDTO == null){throw new IllegalArgumentException("Driver registration data cannot be null");}
//        validateCredentials(driverRegistrationDTO.email(), driverRegistrationDTO.password());
//
//        UserSec user = createUser(driverRegistrationDTO.email(), driverRegistrationDTO.password(), Role.DRIVER.name());
//        UserSec savedUser = userRepository.save(user);
//
//        Neighborhood neighborhood = neighborhoodRepository.findById(driverRegistrationDTO.neighborhoodId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid neighborhood ID"));
//
//        Driver driver = driverMapper.toEntity(driverRegistrationDTO);
//        driver.setUser(savedUser);
//        driver.setNeighborhood(neighborhood);
//
//        Vehicle vehicle = new Vehicle();
//        List<Vehicle> vehicles = new ArrayList<>();
//        vehicle.setNumberPlate(driverRegistrationDTO.numberPlate());
//        vehicle.setCapacityKg(driverRegistrationDTO.capacity());
//        vehicle.setHasRefrigeration(driverRegistrationDTO.hasRefrigeration());
//        vehicle.setVehicleType(VehicleType.valueOf(driverRegistrationDTO.vehicleType()));
//        vehicle.setDriver(driver);
//        vehicles.add(vehicle);
//        driver.setVehicles(vehicles);
//
//        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//        Point location = geometryFactory.createPoint(new Coordinate(driverRegistrationDTO.longitude(), driverRegistrationDTO.latitude()));
//        driver.setCurrentLocation(location);
//
//        Driver driverSaved = driverRepository.save(driver);
//        Vehicle savedVehicle = driverSaved.getVehicles().get(0);
//
//        return new DriverResponseDTO(
//                driverSaved.getId(),
//                driverSaved.getFirstName(),
//                driverSaved.getLastName(),
//                driverSaved.getTaxIdentification(),
//                savedVehicle.getVehicleType().name(),
//                savedVehicle.getNumberPlate(),
//                savedVehicle.isHasRefrigeration(),
//                driverSaved.getNeighborhood().getId(),
//                savedVehicle.getCapacityKg()
//        );
        return null;
    }

    @Transactional
    @Override
    public OrganizationResponseDTO registerOrganization(OrganizationRegistrationDTO organizationRegistrationDTO) {
//        if(organizationRegistrationDTO == null){throw new IllegalArgumentException("Organization registration data cannot be null");}
//        validateCredentials(organizationRegistrationDTO.email(), organizationRegistrationDTO.password());
//
//        UserSec user = createUser(organizationRegistrationDTO.email(), organizationRegistrationDTO.password(), Role.ORGANIZATION.name());
//        UserSec savedUser = userRepository.save(user);
//
//        Neighborhood neighborhood = neighborhoodRepository.findById(organizationRegistrationDTO.neighborhoodId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid neighborhood ID"));
//
//        Organization organization = organizationMapper.toEntity(organizationRegistrationDTO);
//        organization.setUser(savedUser);
//        organization.setNeighborhood(neighborhood);
//
//        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//        Point location = geometryFactory.createPoint(new Coordinate(organizationRegistrationDTO.longitude(), organizationRegistrationDTO.latitude()));
//        organization.setLocation(location);
//
//        Organization savedOrganization = organizationRepository.save(organization);
//
//        return organizationMapper.toResponseDTO(savedOrganization);
        return null;
    }

    @Override
    public AuthResponseDTO login(AuthLoginRequestDTO request) {
//        UserSec user = userRepository.findByEmail(request.email())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
//        if (!user.getPassword().equals(request.password())) {
//            throw new IllegalArgumentException("Invalid credentials");
//        }
//        return new LoginResponseDTO(user.getId(), user.getEmail(), user.getRole().name());
          return  null;
    }

    private UserSec createUser(String email,String password, String role) {
//        UserSec user = new UserSec();
//        user.setEmail(email);
//        //TODO encrypt password
//        user.setPassword(password);
//        user.setRole(Role.valueOf(role));
//        return user;
        return null;
    }
//
//    private void validateCredentials(String email, String password) {
//        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
//        if (email == null || !email.matches(emailRegex)) {
//            throw new IllegalArgumentException("Format email invalid.");
//        }
//
//        //una letra minúscula, una letra mayúscula, un número, un carácter especial y una longitud de al menos 8
//        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";
//        if (password == null || !password.matches(passwordRegex)) {
//            throw new IllegalArgumentException("Password doesn't meet the security requirements.");
//        }
//    }
}
