package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.ReceptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceptionRecordRepository extends JpaRepository<ReceptionRecord, Long> {
    Optional<ReceptionRecord> findByDonationId(Long donationId);
    List<ReceptionRecord> findByDonation_Donor_IdAndAcceptanceTimestampBetween(Long donorId, LocalDateTime start, LocalDateTime end);
}
