package com.tfi.econexo.repositories.donation;

import com.tfi.econexo.entities.donation.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
}
