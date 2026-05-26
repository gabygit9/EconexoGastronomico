package com.tfi.econexo.repository.ngo;

import com.tfi.econexo.entity.ngo.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NgoRepository extends JpaRepository<Ngo, Long> {
    Optional<Ngo> findByTaxId(String taxId);

    Optional<Ngo> findByLegalPersonalityNumber(String legalPersonalityNumber);
}
