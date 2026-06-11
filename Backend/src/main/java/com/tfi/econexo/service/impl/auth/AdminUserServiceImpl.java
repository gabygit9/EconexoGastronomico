package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.admin.UserAdminResponseDTO;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.donation.DonorRepository;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.repository.ngo.NgoRepository;
import com.tfi.econexo.service.auth.AdminUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final DonorRepository donorRepository;
    private final NgoRepository ngoRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserAdminResponseDTO> getAllRegisteredUsers() {
        List<UserAdminResponseDTO> combinedList = new ArrayList<>();

        donorRepository.findAll().forEach(donor -> combinedList.add(new UserAdminResponseDTO(
                donor.getUser().getId(),
                donor.getTradeName(),
                donor.getUser().getEmail(),
                "DONOR",
                donor.getStatus().name(),
                donor.getCreatedDate(),
                donor.getTaxId(),
                null,
                null)));

        driverRepository.findAll().forEach(driver -> combinedList.add(new UserAdminResponseDTO(
                driver.getUser().getId(),
                driver.getLastName() + ", " + driver.getFirstName(),
                driver.getUser().getEmail(),
                "DRIVER",
                driver.getStatus().name(),
                driver.getCreatedDate(),
                driver.getTaxId(),
                driver.getFoodHandlerCertificateUrl(),
                null)));

        ngoRepository.findAll().forEach(ngo -> combinedList.add(new UserAdminResponseDTO(
                ngo.getUser().getId(),
                ngo.getNgoName(),
                ngo.getUser().getEmail(),
                "NGO",
                ngo.getStatus().name(),
                ngo.getCreatedDate(),
                ngo.getTaxId(),
                null,
                ngo.getLegalPersonalityNumber())));

        combinedList.sort(Comparator.comparing(UserAdminResponseDTO::createdDate).reversed());
        return combinedList;
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, RegistrationStatus status) {
        UserSec user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String role = user.getRolesList().iterator().next().getRole();

        if(role.contains("DONOR")){
            donorRepository.findByUser_Email(user.getEmail()).ifPresentOrElse(
                    donor -> {
                        donor.setStatus(status);
                        donorRepository.save(donor);
                    },
                    () -> {
                        throw new EntityNotFoundException("Donor not found");
                    }
            );
        } else if(role.contains("DRIVER")){
            driverRepository.findByUser_Email(user.getEmail()).ifPresentOrElse(
                    driver -> {
                        driver.setStatus(status);
                        driverRepository.save(driver);
                    },
                    () -> {
                        throw new EntityNotFoundException("Driver not found");
                    }
            );
        } else if(role.contains("NGO")){
            ngoRepository.findByUser_Email(user.getEmail()).ifPresentOrElse(
                    ngo -> {
                        ngo.setStatus(status);
                        ngoRepository.save(ngo);
                    },
                    () -> {
                        throw new EntityNotFoundException("NGO not found");
                    }
            );
        } else {
            throw new IllegalArgumentException("The selected user is not a donor, driver or NGO");
        }
    }
}
