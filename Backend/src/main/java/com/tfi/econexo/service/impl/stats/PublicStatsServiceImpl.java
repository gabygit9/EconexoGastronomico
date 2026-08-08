package com.tfi.econexo.service.impl.stats;

import com.tfi.econexo.dto.stats.LandingStatsDTO;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.DonorRepository;
import com.tfi.econexo.repository.donation.MoneyDonationRepository;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.repository.ngo.NgoRepository;
import com.tfi.econexo.service.stats.PublicStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PublicStatsServiceImpl implements PublicStatsService {

    private final DonationRepository donationRepository;
    private final MoneyDonationRepository moneyDonationRepository;
    private final NgoRepository ngoRepository;
    private final DriverRepository driverRepository;
    private final DonorRepository donorRepository;

    @Override
    public LandingStatsDTO getLandingStats() {
        Double totalKilos = donationRepository.sumAllDeliveredKilos();
        Long totalDeliveries = donationRepository.countAllDeliveredDonations();
        Double totalMoney = moneyDonationRepository.sumAllDonatedAmount();

        return new LandingStatsDTO(
                totalKilos != null ? BigDecimal.valueOf(totalKilos) : BigDecimal.ZERO,
                totalDeliveries != null ? totalDeliveries : 0L,
                totalMoney != null ? BigDecimal.valueOf(totalMoney) : BigDecimal.ZERO,
                ngoRepository.countActiveNgos(),
                donorRepository.countActiveDonors(),
                driverRepository.countActiveDrivers()
        );
    }
}
