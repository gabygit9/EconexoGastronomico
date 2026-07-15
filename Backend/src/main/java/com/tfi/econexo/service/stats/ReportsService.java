package com.tfi.econexo.service.stats;

import com.tfi.econexo.dto.stats.AdminStatsDTO;
import com.tfi.econexo.dto.stats.DonorStatsDTO;
import com.tfi.econexo.dto.stats.DriverStatsDTO;
import com.tfi.econexo.dto.stats.NgoStatsDTO;

public interface ReportsService {

    Object getStatsByRole(String role, String username);
    NgoStatsDTO getNgoStats(String email);
    DonorStatsDTO getDonorStats(String email);
    DriverStatsDTO getDriverStats(String email);
    AdminStatsDTO getAdminStats(String email);
}
