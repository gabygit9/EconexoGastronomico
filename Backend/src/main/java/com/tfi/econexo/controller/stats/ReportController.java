package com.tfi.econexo.controller.stats;

import com.tfi.econexo.service.stats.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for reports")
public class ReportController {

    private final ReportsService reportsService;

    @GetMapping("/stats")
    @Operation(summary = "Get stats by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stats retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getStats(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = authentication.getName();

        return ResponseEntity.ok(reportsService.getStatsByRole(role, username));
    }
}
