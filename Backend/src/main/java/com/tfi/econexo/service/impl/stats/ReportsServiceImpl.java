package com.tfi.econexo.service.impl.stats;

import com.tfi.econexo.dto.stats.*;
import com.tfi.econexo.dto.stats.donor.*;
import com.tfi.econexo.dto.stats.driver.DriverStatsComparisonDTO;
import com.tfi.econexo.dto.stats.driver.DriverStatsDTO;
import com.tfi.econexo.dto.stats.driver.MonthlyDriverTrendDTO;
import com.tfi.econexo.dto.stats.driver.TopBusinessDTO;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private final DonationRepository donationRepository;
    private final MoneyDonationRepository moneyDonationRepository;

    private static final LocalDateTime DEFAULT_RANGE_START = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final double RATIONS_PER_KG = 2.0;

    @Override
    public Object getStatsByRole(String role, String username, LocalDate startDate, LocalDate endDate) {
        return switch (role) {
            case "ROLE_NGO" -> getNgoStats(username);
            case "ROLE_DONOR" -> getDonorStats(username, startDate, endDate);
            case "ROLE_DRIVER" -> getDriverStats(username, startDate, endDate);
            case "ROLE_ADMIN" -> getAdminStats(username, role, startDate, endDate);
            default -> throw new RuntimeException("Invalid role");
        };
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
    public DonorStatsDTO getDonorStats(String email, LocalDate startDate, LocalDate endDate) {
        boolean hasDateFilter = startDate != null && endDate != null;
        //Fechas
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : DEFAULT_RANGE_START;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : LocalDateTime.now();
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime startOfPrevMonth = startOfMonth.minusMonths(1);

        //comida
        Double totalKilos = donationRepository.sumQuantityByDonorBetween(email, start, end);
        Long totalDonations = donationRepository.countTotalDonationsByDonorBetween(email, start, end);
        Long completedDonations = donationRepository.countCompletedDonationsByDonorBetween(email, start, end);

        Double totalMoney = moneyDonationRepository.sumDonatedAmountByDonorBetween(email, start, end);

        Double successRate = (totalDonations == null || totalDonations == 0) ? 0.0 :
                (completedDonations != null ? completedDonations : 0L) * 100.0 / totalDonations;

        Double currentMonthImpact = donationRepository.sumQuantityByDonorAndDateRange(email, startOfMonth, LocalDateTime.now());
        Double prevMonthImpact = donationRepository.sumQuantityByDonorAndDateRange(email, startOfPrevMonth, startOfMonth);
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

        List<TopNgoDTO> topNgos = donationRepository.getTopNgosByDonor(email, start, end, PageRequest.of(0,5)).stream()
                .map(obj -> new TopNgoDTO((String) obj[0], (Double) obj[1]))
                .toList();

        List<MonthlyDonorTrendDTO> monthlyTrend = buildDonorMonthlyTrend(email, start, end);

        List<Object[]> funnel = donationRepository.getDonationFunnelByDonor(email, start, end);
        List<Object[]> heatmap = donationRepository.getDonationHeatmapByDonor(email, start, end);

        Double estimatedRations = (totalKilos != null ? totalKilos : 0.0) * RATIONS_PER_KG;

        DonorStatsComparisonDTO comparison = null;
        if (hasDateFilter) {
            comparison = comparisonDonorPreviousPeriod(email, startDate, endDate, start, totalDonations, totalKilos, totalMoney, completedDonations);
        }

        return new DonorStatsDTO(
                totalKilos != null ? totalKilos : 0.0,
                totalMoney != null ? totalMoney : 0.0,
                totalDonations != null ? totalDonations : 0L,
                categories,
                currentMonthImpact != null ? currentMonthImpact : 0.0,
                prevMonthImpact != null ? prevMonthImpact : 0.0,
                currentMoney != null ? currentMoney : 0.0,
                prevMoney != null ? prevMoney : 0.0,
                recentDonations,
                completedDonations != null ? completedDonations : 0L,
                successRate,
                estimatedRations,
                topNgos,
                monthlyTrend,
                comparison,
                funnel,
                heatmap);
    }

    private DonorStatsComparisonDTO comparisonDonorPreviousPeriod(String email, LocalDate startDate, LocalDate endDate, LocalDateTime start, Long totalDonations, Double totalKilos, Double totalMoney, Long completedDonations){
        long durationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDateTime prevEnd = start.minusSeconds(1);
        LocalDateTime prevStart = start.minusDays(durationDays);

        Double prevKilos = donationRepository.sumQuantityByDonorBetween(email, prevStart, prevEnd);
        Long prevDonationsCount = donationRepository.countTotalDonationsByDonorBetween(email, prevStart, prevEnd);
        Double prevMoneyRange = moneyDonationRepository.sumDonatedAmountByDonorBetween(email, prevStart, prevEnd);
        Long prevCompleted = donationRepository.countCompletedDonationsByDonorBetween(email, prevStart, prevEnd);

        return new DonorStatsComparisonDTO(
                percentChange(totalKilos, prevKilos),
                prevKilos != null ? prevKilos : 0.0,
                percentChange(totalMoney, prevMoneyRange),
                prevMoneyRange != null ? prevMoneyRange : 0.0,
                percentChange(totalDonations, prevDonationsCount),
                prevDonationsCount != null ? prevDonationsCount : 0L,
                percentChange(completedDonations, prevCompleted),
                prevCompleted != null ? prevCompleted : 0L
        );
    }

    private List<MonthlyDonorTrendDTO> buildDonorMonthlyTrend(String email, LocalDateTime start, LocalDateTime end) {
        Map<String, Double> kilosByMonth = new LinkedHashMap<>();
        Map<String, Double> moneyByMonth = new LinkedHashMap<>();

        for (Object[] row : donationRepository.getMonthlyKilosTrendByDonor(email, start, end)) {
            String key = ((Number) row[0]).intValue() + "-" + ((Number) row[1]).intValue();
            kilosByMonth.put(key, row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
        }
        for (Object[] row : moneyDonationRepository.getMonthlyMoneyTrendByDonor(email, start, end)) {
            String key = ((Number) row[0]).intValue() + "-" + ((Number) row[1]).intValue();
            moneyByMonth.put(key, row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
        }

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(kilosByMonth.keySet());
        allMonths.addAll(moneyByMonth.keySet());

        List<MonthlyDonorTrendDTO> result = new ArrayList<>();
        for (String key : allMonths) {
            String[] parts = key.split("-");
            result.add(new MonthlyDonorTrendDTO(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    kilosByMonth.getOrDefault(key, 0.0),
                    moneyByMonth.getOrDefault(key, 0.0)
            ));
        }
        return result;
    }

    @Override
    public DriverStatsDTO getDriverStats(String email, LocalDate startDate, LocalDate endDate) {
        boolean hasDateFilter = startDate != null && endDate != null;

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : DEFAULT_RANGE_START;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : LocalDateTime.now();

        List<Donation> deliveries = donationRepository.findCompletedDeliveriesByDriverBetween(email, start, end);
        Long totalDeliveries = donationRepository.countDeliveriesByDriverBetween(email, start, end);
        Double totalKilos = donationRepository.sumQuantitiesTransportedByDriverBetween(email, start, end);
        Long punctual = donationRepository.countPunctualDeliveriesByDriverBetween(email, start, end);

        List<Donation> validDeliveries = deliveries.stream()
                .filter(d -> d.getDeliveryEvidence() != null && d.getDeliveryEvidence().getAcceptedAt() != null)
                .toList();

        //Actividad por hora (Array de 24 posiciones)
        List<Integer> activityByHour = new ArrayList<>(java.util.Collections.nCopies(24,0));
        validDeliveries.forEach(d -> {
            int hour = d.getDeliveryEvidence().getAcceptedAt().getHour();
            activityByHour.set(hour, activityByHour.get(hour) + 1);
        });

        List<Map<String, Object>> monthlyPunctuality = donationRepository.getMonthlyPunctuality(email, start, end).stream()
                .filter(obj -> obj[0] != null && obj[1] != null)
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", ((Number) obj[0]).intValue());
                    map.put("value", ((Number) obj[1]).doubleValue());
                    return map;
                })
                .toList();

        Double avgKilos = (totalDeliveries != null && totalDeliveries > 0 && totalKilos != null) ? (totalKilos / totalDeliveries) : 0.0;

        long activeDays = validDeliveries.stream()
                .map(d -> d.getDeliveryEvidence().getAcceptedAt().toLocalDate())
                .distinct()
                .count();

        double totalDist = deliveries.stream()
                .mapToDouble(d -> calculateDistance(d.getDonor().getLocation(), d.getNgo().getLocation()))
                .sum();

        Double punctualityPercentage = (totalDeliveries == null || totalDeliveries == 0) ? 0.0 :
                (double) (punctual != null ? punctual : 0) / totalDeliveries * 100;

        List<Object[]> funnel = donationRepository.getDonationFunnelByDriver(email, start, end);

        List<TopBusinessDTO> topBusinesses = donationRepository.getTopBusinessesByDriver(email, start, end, PageRequest.of(0, 5)).stream()
                .map(obj -> new TopBusinessDTO((String) obj[0], (Double) obj[1]))
                .toList();

        List<TopNgoDTO> topNgos = donationRepository.getTopNgosByDriver(email, start, end, PageRequest.of(0, 5)).stream()
                .map(obj -> new TopNgoDTO((String) obj[0], (Double) obj[1]))
                .toList();

        List<MonthlyDriverTrendDTO> monthlyTrend = donationRepository.getMonthlyDeliveryTrendByDriver(email, start, end).stream()
                .map(obj -> new MonthlyDriverTrendDTO(
                        ((Number) obj[0]).intValue(),
                        ((Number) obj[1]).intValue(),
                        ((Number) obj[2]).longValue(),
                        obj[3] != null ? ((Number) obj[3]).doubleValue() : 0.0))
                .toList();

        DriverStatsComparisonDTO comparison = null;
        if (hasDateFilter) {
            comparison = comparisonDriverPreviousPeriod(startDate, endDate, start, email, totalDeliveries, totalKilos, punctualityPercentage);
        }

        return new DriverStatsDTO(
                totalDeliveries != null ? totalDeliveries : 0L,
                totalKilos != null ? totalKilos : 0.0,
                deliveries.isEmpty() ? 0.0 : totalDist / deliveries.size(),
                punctualityPercentage,
                activityByHour,
                avgKilos,
                activeDays,
                monthlyPunctuality,
                funnel,
                topBusinesses,
                topNgos,
                monthlyTrend,
                comparison
        );
    }

    private DriverStatsComparisonDTO comparisonDriverPreviousPeriod(LocalDate startDate, LocalDate endDate, LocalDateTime start, String email, Long totalDeliveries, Double totalKilos, Double punctualityPercentage){
        long durationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDateTime prevEnd = start.minusSeconds(1);
        LocalDateTime prevStart = start.minusDays(durationDays);

        Long prevDeliveries = donationRepository.countDeliveriesByDriverBetween(email, prevStart, prevEnd);
        Double prevKilos = donationRepository.sumQuantitiesTransportedByDriverBetween(email, prevStart, prevEnd);
        Long prevPunctual = donationRepository.countPunctualDeliveriesByDriverBetween(email, prevStart, prevEnd);
        Double prevPunctuality = (prevDeliveries == null || prevDeliveries == 0) ? 0.0 :
                (double) (prevPunctual != null ? prevPunctual : 0) / prevDeliveries * 100;

        return new DriverStatsComparisonDTO(
                percentChange(totalDeliveries, prevDeliveries),
                prevDeliveries != null ? prevDeliveries : 0L,
                percentChange(totalKilos, prevKilos),
                prevKilos != null ? prevKilos : 0.0,
                punctualityPercentage - prevPunctuality,
                prevPunctuality
        );
    }

    @Override
    public Map<String, Object> getAdminStats(String email, String role, LocalDate startDate, LocalDate endDate) {
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
            comparisonAdminPreviousPeriod(startDate, endDate, start, totalDonations, totalKilos, completedDeliveries, totalMoney, networkPunctuality, stats);
        }

        return stats;
    }

    private void comparisonAdminPreviousPeriod(LocalDate startDate, LocalDate endDate, LocalDateTime start, Long totalDonations, Double totalKilos, Long completedDeliveries, Double totalMoney, Double networkPunctuality, Map<String, Object> stats){
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
