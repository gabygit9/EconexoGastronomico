package com.tfi.econexo.controller.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.service.logistics.LogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logistics operations")
public class LogisticsController {

    private final LogisticsService logisticsService;

    @GetMapping("/available-trips")
    @Operation(summary = "Get available trips by location",
            description = "Return a pending of retire donations list compatible with driver's vehicles, ordered by distance.")
    @ApiResponse(responseCode = "200", description = "A list of available trips returned successfully")
    public ResponseEntity<List<DonationResponseDTO>> getAvailableTrips (Principal principal,
                                                                        @RequestParam Double latitude,
                                                                        @RequestParam Double longitude){
        String driverEmail = principal.getName();
        return ResponseEntity.ok(logisticsService.getAvailableTripsNearby(driverEmail, latitude, longitude));
    }
}
