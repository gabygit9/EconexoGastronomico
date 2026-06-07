package com.tfi.econexo.service;

import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.model.ngo.Ngo;

import java.util.Optional;

public interface NgoService {

    Optional<Ngo> findByTaxId(String taxId);
    Optional<Ngo> findByLegalPersonalityNumber(String legalPersonalityNumber);
    Ngo save(Ngo ngo);
    boolean existsEmail(String email);
    NgoResponseDTO getProfileByEmail(String email);
}
