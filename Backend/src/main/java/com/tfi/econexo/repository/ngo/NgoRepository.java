package com.tfi.econexo.repository.ngo;

import com.tfi.econexo.model.ngo.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NgoRepository extends JpaRepository<Ngo, Long> {
    Optional<Ngo> findByTaxId(String taxId);

    Optional<Ngo> findByLegalPersonalityNumber(String legalPersonalityNumber);

    boolean existsByUser_Email(String userEmail);

    Optional<Ngo> findByUser_Email(String userEmail);

    @Query("SELECT n FROM Ngo n WHERE n.user.isActive = true")
    List<Ngo> findAllActive();

    @Query("SELECT COUNT(n) FROM Ngo n WHERE n.user.isActive = true")
    long countActiveNgos();
}
