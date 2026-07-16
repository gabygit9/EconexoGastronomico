package com.tfi.econexo.service.impl.stats;

import com.tfi.econexo.dto.stats.*;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.MoneyDonationRepository;
import com.tfi.econexo.service.stats.ReportsService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private final DonationRepository donationRepository;
    private final MoneyDonationRepository moneyDonationRepository;
    private final UserRepository userRepository;

    @Override
    public Object getStatsByRole(String role, String username) {
        switch (role) {
            case "ROLE_NGO":
                return getNgoStats(username);
            case "ROLE_DONOR":
                return getDonorStats(username);
            case "ROLE_DRIVER":
                return getDriverStats(username);
            case "ROLE_ADMIN":
                return getAdminStats(username);
            default:
                throw new RuntimeException("Invalid role");
        }
    }

    @Override
    public NgoStatsDTO getNgoStats(String email) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime startOfPrevMonth = startOfMonth.minusMonths(1);

        Double totalKilos = donationRepository.sumQuantityByNgo(email);
        Long uniqueDonors = donationRepository.countUniqueDonorsByNgo(email);
        Long totalRequested = donationRepository.countTotalRequestedByNgo(email);

        Double efficiency = (totalRequested == null || totalRequested == 0) ? 0.0 :
                (totalKilos != null ? totalKilos : 0.0) / totalRequested;

        Double currentMonth = donationRepository.sumQuantityByNgoAndDateRange(email, startOfMonth, LocalDateTime.now());
        Double prevMonth = donationRepository.sumQuantityByNgoAndDateRange(email, startOfPrevMonth, startOfMonth);

        Double currentMoney = moneyDonationRepository.sumMoneyByNgoAndDateRange(email, startOfMonth, LocalDateTime.now());
        Double totalMoney = moneyDonationRepository.sumMoneyReceivedByNgo(email);

        List<Object[]> rawTopCategories = donationRepository.getTopCategoriesByNgo(email);
        List<CategoryStatsDTO> topCategories = rawTopCategories.stream()
                .map(obj -> new CategoryStatsDTO((String) obj[0], (Double) obj[1]))
                .toList();

        List<RecentDonationDTO> recentDonations = donationRepository.findRecentDonationsByNgo(email, PageRequest.of(0, 5))
                .stream().map(d -> new RecentDonationDTO(d.getDonor().getTradeName(), d.getCreatedDate(), d.getDonationItems().stream().map(DonationItem::getQuantity).reduce(0.0, Double::sum)))
                .toList();

        return new NgoStatsDTO(
                totalKilos != null ? totalKilos : 0.0,
                uniqueDonors != null ? uniqueDonors : 0L,
                efficiency,
                currentMonth != null ? currentMonth : 0.0,
                prevMonth != null ? prevMonth : 0.0,
                totalMoney != null ? totalMoney : 0.0,
                currentMoney != null ? currentMoney : 0.0,
                topCategories,
                recentDonations);
    }

    @Override
    public DonorStatsDTO getDonorStats(String email) {
        //Fechas
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime startOfPrevMonth = startOfMonth.minusMonths(1);

        //comida
        Double totalKilos = donationRepository.sumQuantityByDonor(email);
        Double currentMonthImpact = donationRepository.sumQuantityByDonorAndDateRange(email, startOfMonth, LocalDateTime.now());
        Double prevMonthImpact = donationRepository.sumQuantityByDonorAndDateRange(email, startOfPrevMonth, startOfMonth);
        Long totalDonations = donationRepository.countTotalDonationsByDonor(email);

        //dinero
        Double totalMoney = moneyDonationRepository.sumDonatedAmountByDonor(email);
        Double currentMoney = moneyDonationRepository.sumMoneyByDonorAndDateRange(email, startOfMonth, LocalDateTime.now());
        Double prevMoney = moneyDonationRepository.sumMoneyByDonorAndDateRange(email, startOfPrevMonth, startOfMonth);

        //categorías
        List<CategoryStatsDTO> categories = donationRepository.getMostDonatedCategories(email).stream()
                .map(obj -> new CategoryStatsDTO((String) obj[0], (Double) obj[1]))
                .toList();

        //últimas 5 donaciones
        List<RecentDonationDTO> recentDonations = donationRepository.findRecentDonationsByDonor(email, PageRequest.of(0, 5))
                .stream()
                .map(d -> new RecentDonationDTO(
                        d.getNgo().getNgoName(),
                        d.getCreatedDate(),
                        d.getDonationItems().stream().mapToDouble(DonationItem::getQuantity).sum()))
                .toList();

        return new DonorStatsDTO(
                totalKilos != null ? totalKilos : 0.0,
                totalMoney != null ? totalMoney : 0.0,
                totalDonations != null ? totalDonations : 0L,
                categories,
                currentMonthImpact != null ? currentMonthImpact : 0.0,
                prevMonthImpact != null ? prevMonthImpact : 0.0,
                currentMoney != null ? currentMoney : 0.0,
                prevMoney != null ? prevMoney : 0.0,
                recentDonations);
    }

    @Override
    public DriverStatsDTO getDriverStats(String email) {
        List<Donation> deliveries = donationRepository.findCompletedDeliveriesByDriver(email);
        Long totalDeliveries = donationRepository.countDeliveriesByDriver(email);
        Double totalKilos = donationRepository.sumQuantitiesTransportedByDriver(email);
        Long punctual = donationRepository.countPunctualDeliveriesByDriver(email);

        double totalDist = 0;
        for(Donation d : deliveries){
            totalDist += calculateDistance(d.getDonor().getLocation(), d.getNgo().getLocation());
        }

        return new DriverStatsDTO(
                totalDeliveries != null ? totalDeliveries : 0L,
                totalKilos != null ? totalKilos : 0.0,
                deliveries.isEmpty() ? 0.0 : totalDist / deliveries.size(),
                totalDeliveries == null || totalDeliveries == 0 ? 0.0 : (double) (punctual != null ? punctual : 0) / totalDeliveries * 100
        );
    }

    @Override
    public AdminStatsDTO getAdminStats(String email) {
        Long totalDonations = donationRepository.count();
        Long totalUsers = userRepository.count();
        Double totalMoney = moneyDonationRepository.sumAllDonatedAmount();

        return new AdminStatsDTO(totalDonations, totalUsers, totalMoney);
    }

    private double calculateDistance(Point p1, Point p2) {
        if(p1 == null || p2 == null) return 0.0;

        final int R = 6371; // radio de la tierra en km

        double lat1 = p1.getY();
        double lon1 = p1.getX();
        double lat2 = p2.getY();
        double lon2 = p2.getX();

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 -lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
