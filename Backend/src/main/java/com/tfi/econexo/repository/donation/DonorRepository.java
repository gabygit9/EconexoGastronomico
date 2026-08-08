package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.donor.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    Boolean existsByTaxId(String taxId);

    Boolean existsByUser_Email(String email);

    Optional<Donor> findByUser_Email(String email);

    @Query("SELECT COUNT(d) FROM Donor d WHERE d.user.isActive = true")
    long countActiveDonors();
}
