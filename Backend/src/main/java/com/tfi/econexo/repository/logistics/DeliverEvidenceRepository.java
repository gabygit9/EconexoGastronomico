package com.tfi.econexo.repository.logistics;

import com.tfi.econexo.model.logistics.DeliveryEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliverEvidenceRepository extends JpaRepository<DeliveryEvidence, Long> {
    Optional<DeliveryEvidence> findByDonationId(Long donationId);
}
