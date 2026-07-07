package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.ReceptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceptionRecordRepository extends JpaRepository<ReceptionRecord, Long> {
    Optional<ReceptionRecord> findByDonationId(Long donationId);
}
