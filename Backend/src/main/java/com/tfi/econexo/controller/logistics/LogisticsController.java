package com.tfi.econexo.controller.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.logistics.AcceptTripRequestDTO;
import com.tfi.econexo.service.logistics.LogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logistics operations")
public class LogisticsController {

    private final LogisticsService logisticsService;

    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    @PostMapping("/trips/{id}/accept")
    @Operation(summary = "Accept a donation trip", description = "Accept a donation trip by a driver")
    @ApiResponse(responseCode = "200", description = "Donation trip accepted successfully")
    public ResponseEntity<Void> acceptTrip(Principal principal, @PathVariable Long id, @RequestBody @Valid AcceptTripRequestDTO request){
        String driverEmail = principal.getName();
        logisticsService.acceptTrip(id, driverEmail, request.vehicleId());
        return ResponseEntity.noContent().build();
    }
}
