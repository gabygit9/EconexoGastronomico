package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT d FROM Donation d WHERE d.status = 'AVAILABLE' AND NOT EXISTS " +
            "(SELECT 1 FROM DonationItem i WHERE i.donation = d AND i.expirationDate <= CURRENT TIMESTAMP )")
    List<Donation> findByStatusAvailableAndNotExpired();

    @Query("SELECT DISTINCT d FROM Donation d INNER JOIN d.donationItems i WHERE d.status = 'AVAILABLE' " +
            "AND i.expirationDate <= :date")
    List<Donation> findDonationsToExpire(LocalDateTime date);
}

