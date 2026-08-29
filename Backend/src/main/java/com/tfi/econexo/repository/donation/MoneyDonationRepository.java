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
import java.util.List;

@Repository
public interface MoneyDonationRepository extends JpaRepository<MoneyDonation, Long>, JpaSpecificationExecutor<MoneyDonation> {

    Page<MoneyDonationDTO> findByNgo_Id(Long ngoId, Pageable pageable);

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

    @Query("SELECT SUM(md.amount) FROM MoneyDonation md WHERE md.donor.user.email = :email AND md.status = 'COMPLETED' AND md.createdDate BETWEEN :start AND :end")
    Double sumDonatedAmountByDonorBetween(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT EXTRACT(YEAR FROM md.created_date) AS yr, EXTRACT(MONTH FROM md.created_date) AS mo, " +
            "COALESCE(SUM(md.amount), 0) AS money " +
            "FROM money_donations md " +
            "JOIN donors dn ON md.donor_id = dn.id " +
            "JOIN users u ON dn.user_id = u.id " +
            "WHERE u.email = :email AND md.status = 'COMPLETED' AND md.created_date BETWEEN :start AND :end " +
            "GROUP BY yr, mo ORDER BY yr, mo",
            nativeQuery = true)
    List<Object[]> getMonthlyMoneyTrendByDonor(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(m.amount) FROM MoneyDonation m WHERE m.ngo.user.email = :email AND m.createdDate BETWEEN :start AND :end")
    Double sumMoneyReceivedByNgoBetween(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT EXTRACT(YEAR FROM md.created_date) AS yr, EXTRACT(MONTH FROM md.created_date) AS mo, " +
            "COALESCE(SUM(md.amount), 0) AS money " +
            "FROM money_donations md " +
            "JOIN organizations n ON md.ngo_id = n.id " +
            "JOIN users u ON n.user_id = u.id " +
            "WHERE u.email = :email AND md.status = 'COMPLETED' AND md.created_date BETWEEN :start AND :end " +
            "GROUP BY yr, mo ORDER BY yr, mo",
            nativeQuery = true)
    List<Object[]> getMonthlyMoneyTrendByNgo(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
