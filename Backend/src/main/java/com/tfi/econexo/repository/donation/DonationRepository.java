package com.tfi.econexo.repository.donation;

import com.tfi.econexo.entity.donation.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
}
