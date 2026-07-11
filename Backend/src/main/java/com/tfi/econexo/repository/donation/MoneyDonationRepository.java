package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.MoneyDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyDonationRepository extends JpaRepository<MoneyDonation, Long> {
}
