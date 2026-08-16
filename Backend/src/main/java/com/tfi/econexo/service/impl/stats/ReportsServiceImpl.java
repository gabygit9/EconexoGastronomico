package com.tfi.econexo.service.impl.stats;

import com.tfi.econexo.dto.stats.*;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.MoneyDonationRepository;
import com.tfi.econexo.service.stats.ReportsService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private final DonationRepository donationRepository;
    private final MoneyDonationRepository moneyDonationRepository;

    private static final LocalDateTime DEFAULT_RANGE_START = LocalDateTime.of(2000, 1, 1, 0, 0);

    @Override
    public Object getStatsByRole(String role, String username, LocalDate startDate, LocalDate endDate) {
        switch (role) {
            case "ROLE_NGO":
                return getNgoStats(username);
            case "ROLE_DONOR":
                return getDonorStats(username);
            case "ROLE_DRIVER":
                return getDriverStats(username);
            case "ROLE_ADMIN":
                return getAdminStats(username, startDate, endDate);
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

        List<Donation> validDeliveries = deliveries.stream()
                .filter(d -> d.getDeliveryEvidence() != null && d.getDeliveryEvidence().getAcceptedAt() != null)
                .toList();

        //Actividad por hora (Array de 24 posiciones)
        List<Integer> activityByHour = new ArrayList<>(java.util.Collections.nCopies(24,0));
        validDeliveries.forEach(d -> {
            if(d.getDeliveryEvidence() != null && d.getDeliveryEvidence().getAcceptedAt() != null) {
                int hour = d.getDeliveryEvidence().getAcceptedAt().getHour();
                activityByHour.set(hour, activityByHour.get(hour) + 1);
            }
        });

        List<Map<String, Object>> monthlyPunctuality = donationRepository.getMonthlyPunctuality(email).stream()
                .filter(obj -> obj[0] != null && obj[1] != null)
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", ((Number) obj[0]).intValue());
                    map.put("value", ((Number) obj[1]).doubleValue());
                    return map;
                })
                .toList();

        Double avgKilos = (totalDeliveries > 0) ? (totalKilos / totalDeliveries) : 0.0;

        long activeDays = validDeliveries.stream()
                .map(d -> d.getDeliveryEvidence().getAcceptedAt().toLocalDate())
                .distinct()
                .count();

        double totalDist = deliveries.stream()
                .mapToDouble(d -> calculateDistance(d.getDonor().getLocation(), d.getNgo().getLocation()))
                .sum();

        return new DriverStatsDTO(
                totalDeliveries,
                totalKilos != null ? totalKilos : 0.0,
                deliveries.isEmpty() ? 0.0 : totalDist / deliveries.size(),
                totalDeliveries == 0 ? 0.0 : (double) (punctual != null ? punctual : 0) / totalDeliveries * 100,
                activityByHour,
                avgKilos,
                activeDays,
                monthlyPunctuality
        );
    }

    @Override
    public Map<String, Object> getAdminStats(String email, LocalDate startDate, LocalDate endDate) {
        boolean hasDateFilter = startDate != null && endDate != null;

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : DEFAULT_RANGE_START;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : LocalDateTime.now();

        Map<String, Object> stats = new HashMap<>();

        stats.put("heatmap", donationRepository.getDonationHeatmap(start, end));
        stats.put("funnel", donationRepository.getDonationFunnel(start, end));
        stats.put("treemap", donationRepository.getCategoryVolume(start, end));
        stats.put("topDrivers", donationRepository.getTopDrivers(start, end, PageRequest.of(0, 5)));
        stats.put("topNgos", donationRepository.getTopNgos(start, end, PageRequest.of(0, 5)));
        stats.put("monthlyTrend", donationRepository.getMonthlyTrend(start, end));

        Long totalDonations = donationRepository.countAllDonationsInRange(start, end);
        Double totalKilos = donationRepository.sumDeliveredKilosBetween(start, end);
        Long completedDeliveries = donationRepository.countDeliveredDonationsBetween(start, end);
        Double totalMoney = moneyDonationRepository.sumAllDonatedAmountBetween(start, end);
        Double networkPunctuality = donationRepository.getNetworkPunctuality(start, end);

        stats.put("totalDonations", totalDonations != null ? totalDonations : 0L);
        stats.put("totalKilosDelivered", totalKilos != null ? totalKilos : 0.0);
        stats.put("completedDeliveries", completedDeliveries != null ? completedDeliveries : 0L);
        stats.put("totalMoneyDonated", totalMoney != null ? totalMoney : 0.0);
        stats.put("networkPunctuality", networkPunctuality != null ? networkPunctuality : 0.0);

        stats.put("totalDonors", donationRepository.countAllDonors());
        stats.put("totalNgos", donationRepository.countAllNgos());
        stats.put("totalDrivers", donationRepository.countAllDrivers());

        //Comparison of previous period
        if(hasDateFilter){
            comparisonPreviousPeriod(startDate, endDate, start, totalDonations, totalKilos, completedDeliveries, totalMoney, networkPunctuality, stats);
        }

        return stats;
    }

    private void comparisonPreviousPeriod(LocalDate startDate, LocalDate endDate, LocalDateTime start, Long totalDonations, Double totalKilos, Long completedDeliveries, Double totalMoney, Double networkPunctuality, Map<String, Object> stats){
        long durationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDateTime prevEnd = start.minusSeconds(1);
        LocalDateTime prevStart = start.minusDays(durationDays);

        Long prevDonations = donationRepository.countAllDonationsInRange(prevStart, prevEnd);
        Double prevKilos = moneyDonationRepository.sumAllDonatedAmountBetween(prevStart, prevEnd);
        Long prevDeliveries = donationRepository.countDeliveredDonationsBetween(prevStart, prevEnd);
        Double prevMoney = moneyDonationRepository.sumAllDonatedAmountBetween(prevStart, prevEnd);
        Double prevPunctuality = donationRepository.getNetworkPunctuality(prevStart, prevEnd);

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("totalDonations", percentChange(totalDonations, prevDonations));
        comparison.put("totalDonationsPrev", prevDonations != null ? prevDonations : 0L);

        comparison.put("totalKilosDelivered", percentChange(totalKilos, prevKilos));
        comparison.put("totalKilosDeliveredPrev", prevKilos != null ? prevKilos : 0.0);

        comparison.put("completedDeliveries", percentChange(completedDeliveries, prevDeliveries));
        comparison.put("completedDeliveriesPrev", prevDeliveries != null ? prevDeliveries : 0L);

        comparison.put("totalMoneyDonated", percentChange(totalMoney, prevMoney));
        comparison.put("totalMoneyDonatedPrev", prevMoney != null ? prevMoney : 0.0);

        comparison.put("networkPunctualityDelta",
                (networkPunctuality != null ? networkPunctuality : 0.0) - (prevPunctuality != null ? prevPunctuality : 0.0));
        comparison.put("networkPunctualityPrev", prevPunctuality != null ? prevPunctuality : 0.0);

        stats.put("comparison", comparison);
    }

    private Double percentChange(Number current, Number previous){
        double curr = current != null ? current.doubleValue() : 0.0;
        double prev = previous != null ? previous.doubleValue() : 0.0;
        if(prev == 0.0){
            return curr > 0 ? 100.0 : 0.0;
        }
        return ((curr - prev) * 100.0);
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
