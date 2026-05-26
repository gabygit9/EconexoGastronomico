package com.tfi.econexo.service.impl;

import com.tfi.econexo.entity.ngo.Ngo;
import com.tfi.econexo.service.NgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NgoServiceImpl implements NgoService {

    private final NgoService ngoService;

    @Override
    public Optional<Ngo> findByTaxId(String taxId) {
        return ngoService.findByTaxId(taxId);
    }

    @Override
    public Optional<Ngo> findByLegalPersonalityNumber(String legalPersonalityNumber) {
        return ngoService.findByLegalPersonalityNumber(legalPersonalityNumber);
    }

    @Override
    public Ngo save(Ngo ngo) {
        return ngoService.save(ngo);
    }
}
