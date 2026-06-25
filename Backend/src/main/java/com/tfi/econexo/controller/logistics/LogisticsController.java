package com.tfi.econexo.controller.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.logistics.AcceptTripRequestDTO;
import com.tfi.econexo.dto.logistics.TripStatusUpdateRequestDTO;
import com.tfi.econexo.service.donation.DonationService;
import com.tfi.econexo.service.logistics.LogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logistics operations")
public class LogisticsController {

    private final LogisticsService logisticsService;
    private final DonationService donationService;

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

    @PreAuthorize("hasAnyRole('DRIVER', 'NGO', 'DONOR', 'ADMIN')")
    @GetMapping("/trips/{id}")
    @Operation(summary = "Get trip details by ID", description = "Return the details of a specific donation trip.")
    @ApiResponse(responseCode = "200", description = "Trip details returned successfully")
    public ResponseEntity<DonationResponseDTO> getTripById(@PathVariable Long id){
        DonationResponseDTO tripDetails = logisticsService.getTripDetailsById(id);
        return ResponseEntity.ok(tripDetails);
    }

    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    @PatchMapping("/trips/{id}/status")
    @Operation(summary = "Update trip status", description = "Update the status of a donation trip. Move the status trip from ASSIGNED to IN_TRANSIT, or from IN_TRANSIT to DELIVERED.")
    @ApiResponse(responseCode = "204", description = "Trip status updated successfully")
    public ResponseEntity<Void> updateTripStatus(@PathVariable Long id,
                                                 @RequestBody @Valid TripStatusUpdateRequestDTO request,
                                                 Principal principal){

        String driverEmail = principal.getName();
        logisticsService.updateTripStatus(id, request.status(), driverEmail);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/trip/{id}/cancel")
    @Operation(summary = "Cancel an assigned trip", description = "Cancel a donation trip by a driver")
    @ApiResponse(responseCode = "204", description = "Donation trip cancelled successfully")
    public ResponseEntity<Void> cancelTrip(@PathVariable Long id, Authentication authentication){
        String driverEmail = authentication.getName();
        donationService.cancelTrip(id, driverEmail);
        return ResponseEntity.noContent().build();
    }
}
