package com.tfi.econexo.service.stats;

import com.tfi.econexo.dto.stats.donor.DonorStatsDTO;
import com.tfi.econexo.dto.stats.DriverStatsDTO;
import com.tfi.econexo.dto.stats.NgoStatsDTO;

import java.time.LocalDate;
import java.util.Map;

public interface ReportsService {

    Object getStatsByRole(String role, String username, LocalDate startDate, LocalDate endDate);
    NgoStatsDTO getNgoStats(String email);
    DonorStatsDTO getDonorStats(String email, LocalDate startDate, LocalDate endDate);
    DriverStatsDTO getDriverStats(String email);
    Map<String, Object> getAdminStats(String email, String role, LocalDate startDate, LocalDate endDate);

}
