package com.tfi.econexo.repositories.donation;

import com.tfi.econexo.entities.donation.DonationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationDetailRepository extends JpaRepository<DonationDetail, Long> {
}
