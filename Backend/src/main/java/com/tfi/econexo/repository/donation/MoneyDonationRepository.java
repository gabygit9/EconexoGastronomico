package com.tfi.econexo.repository.donation;

import com.tfi.econexo.dto.payment.MoneyDonationDTO;
import com.tfi.econexo.model.donation.MoneyDonation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MoneyDonationRepository extends JpaRepository<MoneyDonation, Long>, JpaSpecificationExecutor<MoneyDonation> {

    Page<MoneyDonationDTO> findByNgo_Id(Long ngoId, Pageable pageable);

    @Query("SELECT SUM(md.amount) FROM MoneyDonation md WHERE md.donor.user.email = :email AND md.status = 'COMPLETED'")
    Double sumDonatedAmountByDonor(@Param("email") String email);

    @Query("SELECT SUM(md.amount) FROM MoneyDonation md WHERE md.status = 'COMPLETED'")
    Double sumAllDonatedAmount();

    @Query("SELECT SUM(m.amount) FROM MoneyDonation m WHERE m.ngo.user.email = :email")
    Double sumMoneyReceivedByNgo(@Param("email") String email);

    @Query("SELECT SUM(m.amount) FROM MoneyDonation m WHERE m.ngo.user.email = :email AND m.createdDate BETWEEN :start AND :end")
    Double sumMoneyByNgoAndDateRange(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(m.amount) FROM MoneyDonation m WHERE m.donor.user.email = :email AND m.createdDate BETWEEN :start AND :end")
    Double sumMoneyByDonorAndDateRange(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(md.amount) FROM MoneyDonation md WHERE md.status = 'COMPLETED' AND md.createdDate BETWEEN :start AND :end")
    Double sumAllDonatedAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
