package com.tfi.econexo.services.impl.auth;

import com.tfi.econexo.dtos.auth.DonorRegistrationDTO;
import com.tfi.econexo.dtos.auth.DonorResponseDTO;
import com.tfi.econexo.entities.auth.Role;
import com.tfi.econexo.entities.auth.User;
import com.tfi.econexo.entities.donation.Donor;
import com.tfi.econexo.entities.donation.DonorType;
import com.tfi.econexo.entities.location.Neighborhood;
import com.tfi.econexo.mappers.DonorMapper;
import com.tfi.econexo.repositories.auth.UserRepository;
import com.tfi.econexo.repositories.donation.DonorRepository;
import com.tfi.econexo.repositories.location.NeighborhoodRepository;
import com.tfi.econexo.services.auth.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final DonorMapper donorMapper;

    @Transactional
    @Override
    public DonorResponseDTO registerDonor(DonorRegistrationDTO donorRegistrationDTO) {
        if(donorRegistrationDTO == null){throw new IllegalArgumentException("Donor registration data cannot be null");}
        if(!isEmailValid(donorRegistrationDTO.email())){throw new IllegalArgumentException("Invalid email");}
        if(!isPasswordValid(donorRegistrationDTO.password())){throw new IllegalArgumentException("Invalid password");}

        User user = new User();
        user.setEmail(donorRegistrationDTO.email());
        //TODO encrypt password
        user.setPassword(donorRegistrationDTO.password());
        user.setRole(Role.DONOR);
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
    
    private boolean isEmailValid(String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    //una letra minúscula, una letra mayúscula, un número, un carácter especial y una longitud de al menos 8
    private boolean isPasswordValid(String password){
        if(password == null) return false;
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";
        return password.matches(passwordRegex);
    }
}
