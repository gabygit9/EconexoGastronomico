package com.tfi.econexo.service;

import com.tfi.econexo.entity.ngo.Ngo;

import java.util.Optional;

public interface NgoService {

    Optional<Ngo> findByTaxId(String taxId);
    Optional<Ngo> findByLegalPersonalityNumber(String legalPersonalityNumber);
    Ngo save(Ngo ngo);
}
