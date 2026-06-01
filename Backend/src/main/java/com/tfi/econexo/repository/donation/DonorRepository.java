package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.donor.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    Boolean existsByTaxId(String taxId);

    Boolean existsByUser_Email(String email);
}
