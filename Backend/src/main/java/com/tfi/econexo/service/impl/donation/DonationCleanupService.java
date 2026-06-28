package com.tfi.econexo.service.impl.donation;

import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.repository.donation.DonationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationCleanupService {

    private final DonationRepository donationRepository;

    // Se ejecuta automáticamente en el minuto 0 de cada hora (ej: 14:00, 15:00, 16:00)
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void expireOldDonations(){
        System.out.println("Starting expired donation cleanup");

        List<Donation> expiredDonations = donationRepository.findDonationsToExpire(LocalDateTime.now());

        for(Donation donation : expiredDonations){
            donation.setStatus(DonationStatus.EXPIRED);
        }

        donationRepository.saveAll(expiredDonations);

        System.out.println("Expired donations cleaned up");
    }
}
