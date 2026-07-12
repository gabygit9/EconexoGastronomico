package com.tfi.econexo.repository.donation;

import com.tfi.econexo.dto.payment.MoneyDonationDTO;
import com.tfi.econexo.model.donation.MoneyDonation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyDonationRepository extends JpaRepository<MoneyDonation, Long>, JpaSpecificationExecutor<MoneyDonation> {
    Page<MoneyDonationDTO> findByNgo_Id(Long ngoId, Pageable pageable);
}
