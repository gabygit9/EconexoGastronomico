package com.tfi.econexo.service.impl.donation;

import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.repository.donation.DonorRepository;
import com.tfi.econexo.service.donation.DonorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonorServiceImpl implements DonorService {

    private final DonorRepository donorRepository;


    @Override
    public Boolean findByTaxId(String taxId) {
        return donorRepository.existsByTaxId(taxId);
    }

    @Override
    public Boolean existsEmail(String email) {
        return donorRepository.existsByUser_Email(email);
    }

    @Override
    public Donor save(Donor donor) {
        return donorRepository.save(donor);
    }

    @Override
    public Optional<Donor> findByUserEmail(String email) {
        return donorRepository.findByUser_Email(email);
    }
}
