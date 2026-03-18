package com.tfi.econexo.repositories.donation;

import com.tfi.econexo.entities.donation.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
}
