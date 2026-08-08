package com.tfi.econexo.controller.stats;

import com.tfi.econexo.dto.stats.LandingStatsDTO;
import com.tfi.econexo.service.stats.PublicStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@Tag(name = "Public Stats", description = "Public aggregate stats for the landing page")
public class PublicStatsController {

    private final PublicStatsService publicStatsService;

    @GetMapping("/landing-stats")
    @Operation(summary = "Get public landing stats", description = "Returns aggregate impact numbers for the landing page, with no sensitive data.")
    public ResponseEntity<LandingStatsDTO> getLandingStats() {
        return ResponseEntity.ok(publicStatsService.getLandingStats());
    }

}
