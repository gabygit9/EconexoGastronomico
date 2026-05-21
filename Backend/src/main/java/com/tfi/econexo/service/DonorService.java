package com.tfi.econexo.service;

import com.tfi.econexo.entity.donation.Donor;

public interface DonorService {
    Boolean findByTaxId(String taxId);
    Boolean findByEmail(String email);
    Donor save(Donor donor);
}
