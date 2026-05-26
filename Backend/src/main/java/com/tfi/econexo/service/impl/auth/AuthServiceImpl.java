package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.NgoRegistrationDTO;
import com.tfi.econexo.dto.NgoResponseDTO;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.entity.donation.Donor;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.ngo.Ngo;
import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.mappers.DonorMapper;
import com.tfi.econexo.mappers.NgoMapper;
import com.tfi.econexo.mappers.UserMapper;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.service.NeighborhoodService;
import com.tfi.econexo.service.NgoService;
import com.tfi.econexo.service.auth.AuthService;
import com.tfi.econexo.service.auth.RoleService;
import com.tfi.econexo.service.auth.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DonorService donorService;
    private final UserService userService;
    private final RoleService roleService;
    private final NgoService ngoService;

    private final DonorMapper donorMapper;
    private final UserMapper userMapper;
    private final NgoMapper ngoMapper;

    private final NeighborhoodService neighborhoodService;

    @Transactional
    @Override
    public DonorResponseDTO registerDonor(DonorRegistrationDTO donorDTO) {

        if(donorDTO == null){
            throw new IllegalArgumentException("Donor registration request cannot be null");
        }

        if (donorService.findByEmail(donorDTO.email()) || donorService.findByTaxId(donorDTO.taxId())) {
            throw new ConflictException("Donor already exists");
        }

        Role role = roleService.findByName("DONOR").orElseThrow(() -> new EntityNotFoundException("Role DONOR not found"));

        String password = userService.encryptPassword(donorDTO.password());

        UserSec user = userMapper.toEntity(donorDTO.email(), password, role);

        Neighborhood neighborhood = neighborhoodService.findById(donorDTO.neighborhoodId())
                .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found"));

        Donor donor = donorMapper.toEntity(donorDTO, user, neighborhood);

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

        Neighborhood neighborhood = neighborhoodService.findById(ngoDTO.neighborhoodId())
                .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found"));

        Role role = roleService.findByName("NGO").orElseThrow(() -> new EntityNotFoundException("Role NGO not found"));

        String password = userService.encryptPassword(ngoDTO.password());

        UserSec user = userMapper.toEntity(ngoDTO.email(), password, role);

        Ngo ngo = ngoMapper.toEntity(ngoDTO, user, neighborhood);

        ngoService.save(ngo);

        return ngoMapper.toResponseDTO(ngo);
    }
}
