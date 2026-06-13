package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.admin.UserAdminResponseDTO;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.ngo.Ngo;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.donation.DonorRepository;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.repository.ngo.NgoRepository;
import com.tfi.econexo.service.auth.AdminUserService;
import com.tfi.econexo.utils.notification.EmailService;
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
    private final EmailService emailService;

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
        String recipientName = "";

        if(role.contains("DONOR")){
            Donor donor = donorRepository.findByUser_Email(user.getEmail()).
                    orElseThrow(() -> new EntityNotFoundException("Donor not found"));
            donor.setStatus(status);
            donorRepository.save(donor);
            recipientName = donor.getTradeName();

        } else if(role.contains("DRIVER")){
            Driver driver = driverRepository.findByUser_Email(user.getEmail()).
                    orElseThrow(() -> new EntityNotFoundException("Driver not found"));
            driver.setStatus(status);
            driverRepository.save(driver);
            recipientName = driver.getFirstName() + " " + driver.getLastName();
        } else if(role.contains("NGO")){
            Ngo ngo = ngoRepository.findByUser_Email(user.getEmail()).
                    orElseThrow(() -> new EntityNotFoundException("NGO not found"));
            ngo.setStatus(status);
            ngoRepository.save(ngo);
            recipientName = ngo.getNgoName();
        } else {
            throw new IllegalArgumentException("The selected user is not a donor, driver or NGO");
        }

        //Mandar mail
        if(status == RegistrationStatus.APPROVED){
            triggerApprovalEmail(user.getEmail(), role, recipientName);
        }
    }

    private void triggerApprovalEmail(String email, String role, String recipientName) {
        emailService.sendApprovalEmail(email, recipientName, role);
    }
}
