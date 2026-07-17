package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT d FROM Donation d JOIN d.donor donor WHERE d.status = :status AND EXISTS (" +
            "SELECT v FROM Vehicle v WHERE v.driver.id = :driverId AND v.capacityKg >= (" +
            "SELECT SUM(di.quantity) FROM DonationItem di WHERE di.donation = d) AND (" +
            "v.hasRefrigeration = true OR NOT EXISTS (SELECT 1 FROM DonationItem di2 JOIN di2.product p WHERE di2.donation = d AND p.requiresRefrigeration = true)))" +
            "AND NOT EXISTS (SELECT 1 FROM DonationItem di3 WHERE di3.donation = d AND di3.expirationDate < CURRENT_TIMESTAMP)" +
            "ORDER BY ST_Distance(donor.location, :driverLocation) ASC")
    List<Donation>  findAvailableTripsNearby(
            @Param("driverLocation") Point driverLocation,
            @Param("driverId") Long driverId,
            @Param("status") DonationStatus status
    );

    @Query("SELECT d FROM Donation d " +
            "LEFT JOIN d.donor don " +
            "LEFT JOIN don.user uDon " +
            "LEFT JOIN d.ngo ngo " +
            "LEFT JOIN ngo.user uNgo " +
            "LEFT JOIN d.driver dr " +
            "LEFT JOIN dr.user uDr " +
            "WHERE uDon.email = :email OR uNgo.email = :email OR uDr.email = :email " +
            "ORDER BY d.createdDate DESC")
    List<Donation> findMyDonationsOrderByCreatedDateDesc(@Param("email") String email);

    // Métricas ONG
    @Query("SELECT SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.ngo.user.email = :email AND d.status = 'DELIVERED'")
    Double sumQuantityByNgo(@Param("email") String email);

    @Query("SELECT COUNT(DISTINCT d.donor.id) FROM Donation d WHERE d.ngo.user.email = :email AND d.status = 'DELIVERED'")
    Long countUniqueDonorsByNgo(@Param("email") String email);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.ngo.user.email = :email")
    Long countTotalRequestedByNgo(@Param("email") String email);

    @Query("SELECT di.product.category.description, SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.ngo.user.email = :email AND d.status = 'DELIVERED' " +
            "GROUP BY di.product.category.description ORDER BY SUM(di.quantity) DESC")
    List<Object[]> getTopCategoriesByNgo(@Param("email") String email);

    @Query("SELECT d FROM Donation d WHERE d.ngo.user.email = :email AND d.status = 'DELIVERED' ORDER BY d.createdDate DESC")
    List<Donation> findRecentDonationsByNgo(@Param("email") String email, Pageable page);

    @Query("SELECT SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.ngo.user.email = :email AND d.status = 'DELIVERED' AND d.createdDate BETWEEN :start AND :end")
    Double sumQuantityByNgoAndDateRange(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Métricas Donante
    @Query("SELECT di.product.category.description, SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.donor.user.email = :email GROUP BY di.product.category.description ORDER BY SUM(di.quantity) DESC")
    List<Object[]> getMostDonatedCategories(@Param("email") String email);

    @Query("SELECT SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.donor.user.email = :email AND d.status = 'DELIVERED'")
    Double sumQuantityByDonor(@Param("email") String email);

    @Query("SELECT d FROM Donation d WHERE d.donor.user.email = :email AND d.status = 'DELIVERED' ORDER BY d.createdDate DESC")
    List<Donation> findRecentDonationsByDonor(@Param("email") String email, Pageable page);

    @Query("SELECT SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.donor.user.email = :email AND d.status = 'DELIVERED' AND d.createdDate BETWEEN :start AND :end")
    Double sumQuantityByDonorAndDateRange(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.donor.user.email = :email")
    Long countTotalDonationsByDonor(@Param("email") String email);

    //Métricas Driver
    @Query("SELECT d FROM Donation d WHERE d.driver.user.email = :email AND d.status = 'DELIVERED'")
    List<Donation> findCompletedDeliveriesByDriver(@Param("email") String email);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.driver.user.email = :email AND d.status = 'DELIVERED'")
    Long countDeliveriesByDriver(@Param("email") String email);

    @Query("SELECT SUM(di.quantity) FROM Donation d JOIN d.donationItems di " +
            "WHERE d.driver.user.email = :email AND d.status = 'DELIVERED'")
    Double sumQuantitiesTransportedByDriver(@Param("email") String email);

    @Query(value = "SELECT EXTRACT(MONTH FROM de.accepted_at) as mes, " +
            "AVG(CASE WHEN de.accepted_at <= d.pickup_end_time THEN 100.0 ELSE 0.0 END) " +
            "FROM donations d " +
            "JOIN delivery_evidences de ON d.id = de.id " +
            "JOIN drivers dr ON d.driver_id = dr.id " +
            "JOIN users u ON dr.user_id = u.id " +
            "WHERE u.email = :email " +
            "AND d.status = 'DELIVERED' " +
            "GROUP BY mes ORDER BY mes ASC",
            nativeQuery = true)
    List<Object[]> getMonthlyPunctuality(@Param("email") String email);

    @Query("SELECT COUNT(d) FROM Donation d WHERE d.driver.user.email = :email AND d.status = 'DELIVERED'" +
            "AND d.deliveryEvidence.acceptedAt <= d.pickupEndTime")
    Long countPunctualDeliveriesByDriver(@Param("email") String email);

}

