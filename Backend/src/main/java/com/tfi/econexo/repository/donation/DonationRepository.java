package com.tfi.econexo.repository.donation;

import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import org.locationtech.jts.geom.Point;
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

}

