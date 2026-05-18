package com.tfi.econexo.repository.donation;

import com.tfi.econexo.entity.donation.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
}
