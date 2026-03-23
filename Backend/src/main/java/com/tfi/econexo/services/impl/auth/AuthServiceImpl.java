package com.tfi.econexo.services.impl.auth;

import com.tfi.econexo.dtos.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dtos.auth.driver.DriverRegistrationDTO;
import com.tfi.econexo.dtos.auth.driver.DriverResponseDTO;
import com.tfi.econexo.entities.auth.Role;
import com.tfi.econexo.entities.auth.User;
import com.tfi.econexo.entities.donation.Donor;
import com.tfi.econexo.entities.location.Neighborhood;
import com.tfi.econexo.entities.logistics.Driver;
import com.tfi.econexo.entities.logistics.Vehicle;
import com.tfi.econexo.entities.logistics.VehicleType;
import com.tfi.econexo.mappers.DonorMapper;
import com.tfi.econexo.mappers.DriverMapper;
import com.tfi.econexo.repositories.auth.UserRepository;
import com.tfi.econexo.repositories.donation.DonorRepository;
import com.tfi.econexo.repositories.location.NeighborhoodRepository;
import com.tfi.econexo.repositories.logistics.DriverRepository;
import com.tfi.econexo.services.auth.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DonorRepository donorRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final DonorMapper donorMapper;
    private final DriverMapper driverMapper;

    @Transactional
    @Override
    public DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO) {
        if(donorRegistrationDTO == null){throw new IllegalArgumentException("Donor registration data cannot be null");}
        validateCredentials(donorRegistrationDTO.email(), donorRegistrationDTO.password());

        User user = createUser(donorRegistrationDTO.email(), donorRegistrationDTO.password(), Role.DONOR.name());

        User savedUser = userRepository.save(user);

        Neighborhood neighborhood = neighborhoodRepository.findById(donorRegistrationDTO.neighborhoodId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid neighborhood ID"));

        Donor donor = donorMapper.toEntity(donorRegistrationDTO);
        donor.setUser(savedUser);
        donor.setNeighborhood(neighborhood);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point location = geometryFactory.createPoint(new Coordinate(donorRegistrationDTO.longitude(), donorRegistrationDTO.latitude()));
        donor.setLocation(location);

        Donor savedDonor = donorRepository.save(donor);

        return donorMapper.toResponseDTO(savedDonor);
    }

    @Transactional
    @Override
    public DriverResponseDTO registerDriver(DriverRegistrationDTO driverRegistrationDTO) {
        if(driverRegistrationDTO == null){throw new IllegalArgumentException("Driver registration data cannot be null");}
        validateCredentials(driverRegistrationDTO.email(), driverRegistrationDTO.password());

        User user = createUser(driverRegistrationDTO.email(), driverRegistrationDTO.password(), Role.DRIVER.name());
        User savedUser = userRepository.save(user);

        Neighborhood neighborhood = neighborhoodRepository.findById(driverRegistrationDTO.neighborhoodId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid neighborhood ID"));

        Driver driver = driverMapper.toEntity(driverRegistrationDTO);
        driver.setUser(savedUser);
        driver.setNeighborhood(neighborhood);

        Vehicle vehicle = new Vehicle();
        List<Vehicle> vehicles = new ArrayList<>();
        vehicle.setNumberPlate(driverRegistrationDTO.numberPlate());
        vehicle.setCapacityKg(driverRegistrationDTO.capacity());
        vehicle.setHasRefrigeration(driverRegistrationDTO.hasRefrigeration());
        vehicle.setVehicleType(VehicleType.valueOf(driverRegistrationDTO.vehicleType()));
        vehicle.setDriver(driver);
        vehicles.add(vehicle);
        driver.setVehicles(vehicles);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point location = geometryFactory.createPoint(new Coordinate(driverRegistrationDTO.longitude(), driverRegistrationDTO.latitude()));
        driver.setCurrentLocation(location);

        Driver driverSaved = driverRepository.save(driver);
        Vehicle savedVehicle = driverSaved.getVehicles().get(0);

        return new DriverResponseDTO(
                driverSaved.getId(),
                driverSaved.getFirstName(),
                driverSaved.getLastName(),
                driverSaved.getTaxIdentification(),
                savedVehicle.getVehicleType().name(),
                savedVehicle.getNumberPlate(),
                savedVehicle.isHasRefrigeration(),
                driverSaved.getNeighborhood().getId(),
                savedVehicle.getCapacityKg()
        );
    }

    private User createUser(String email,String password, String role) {
        User user = new User();
        user.setEmail(email);
        //TODO encrypt password
        user.setPassword(password);
        user.setRole(Role.valueOf(role));
        return user;
    }

    private void validateCredentials(String email, String password) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (email == null || !email.matches(emailRegex)) {
            throw new IllegalArgumentException("Format email invalid.");
        }

        //una letra minúscula, una letra mayúscula, un número, un carácter especial y una longitud de al menos 8
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";
        if (password == null || !password.matches(passwordRegex)) {
            throw new IllegalArgumentException("Password doesn't meet the security requirements.");
        }
    }
}
