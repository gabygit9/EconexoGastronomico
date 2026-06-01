package com.tfi.econexo.repository.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationDetailRepository extends JpaRepository<DonationDetail, Long> {
}
