package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByStatus(DonationStatus status);
}
