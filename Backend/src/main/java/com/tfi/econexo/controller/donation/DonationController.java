package com.tfi.econexo.controller.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.donation.RejectionRequestDTO;
import com.tfi.econexo.dto.donation.summary.DonationSummaryResponseDTO;
import com.tfi.econexo.dto.donation.catalog.CategoryDTO;
import com.tfi.econexo.dto.donation.catalog.ProductDTO;
import com.tfi.econexo.dto.donation.catalog.UnitOfMeasureDTO;
import com.tfi.econexo.dto.reception.DonationItemReceptionDTO;
import com.tfi.econexo.dto.reception.ReceivedDonationDTO;
import com.tfi.econexo.service.donation.CatalogService;
import com.tfi.econexo.service.donation.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
@Tag(name = "Donations", description = "Endpoints for donations")
public class DonationController {

    private final DonationService donationService;
    private final CatalogService catalogService;

    @PreAuthorize("hasRole('DONOR')")
    @PostMapping("/donate")
    @Operation(summary = "Create a new donation",
            description = "Create a new donation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Donation created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<DonationResponseDTO> donate(@Valid @RequestBody DonationRequestDTO request){
        return new ResponseEntity<>(this.donationService.donate(request), HttpStatus.CREATED);
    }

    @GetMapping("/catalog/categories")
    @Operation(summary = "Get all categories",
            description = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved")
    })
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return new ResponseEntity<>(this.catalogService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/catalog/products")
    @Operation(summary = "Get all products",
            description = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved")
    })
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return new ResponseEntity<>(this.catalogService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/catalog/units")
    @Operation(summary = "Get all units",
            description = "Get all units")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Units retrieved")
    })
    public ResponseEntity<List<UnitOfMeasureDTO>> getUnits() {
        return new ResponseEntity<>(this.catalogService.getAllUnits(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('NGO')")
    @GetMapping("/available")
    @Operation(summary = "Get available donations for NGOs",
            description = "Retrieve a list of available donation items, ordered by expiration date ascending")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    })
    public ResponseEntity<List<DonationSummaryResponseDTO>> getAvailableDonations(){
        return new ResponseEntity<>(this.donationService.getAvailableDonationsSummary(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('NGO')")
    @PatchMapping("/{id}/request")
    @Operation(summary = "Request a donation",
            description = "Request a donation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Donation requested successfully")
    })
    public ResponseEntity<Void> requestDonation(@PathVariable Long id, Authentication authentication){
        String email = authentication.getName();
        this.donationService.requestDonation(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('DONOR', 'NGO', 'DRIVER', 'ADMIN')")
    @GetMapping("/me")
    @Operation(summary = "Get my donations",
            description = "Retrieve a list of donations associated with the authenticated user (either as a Donor or an NGO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    })
    public ResponseEntity<List<DonationResponseDTO>> getMyDonations(Authentication authentication){
        String email = authentication.getName();
        return new ResponseEntity<>(this.donationService.getMyDonations(email), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DONOR', 'NGO', 'DRIVER', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get a donation by its id",
            description = "Retrieve a donation by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Donation not found")
    })
    public ResponseEntity<DonationResponseDTO> getDonation(@PathVariable Long id){
        return new ResponseEntity<>(this.donationService.getDonation(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a donation", description = "Allows a donor to completely cancel a donation if it hasn't been picked up yet.")
    public ResponseEntity<Void> cancelDonation(@PathVariable Long id, Authentication authentication){
        String email = authentication.getName();
        this.donationService.cancelDonationByDonor(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    @PostMapping("/{id}/reject-driver")
    @Operation(summary = "Reject assigned driver", description = "Allows a donor to reject a driver (e.g., lack of thermal equipment) and return the donation to the network.")
    public ResponseEntity<Void> rejectDriver(@PathVariable Long id, Authentication authentication){
        String email = authentication.getName();
        this.donationService.rejectDriverByDonor(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    @PostMapping("/{id}/cancel-ngo-donation")
    @Operation(summary = "Cancel a donation by NGO", description = "Allows an NGO to completely cancel a donation if it hasn't been picked up yet.")
    public ResponseEntity<Void> cancelDonationByNGO(@PathVariable Long id, Authentication authentication){
        String email = authentication.getName();
        this.donationService.cancelDonationByNgo(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    @GetMapping("/{id}/items")
    @Operation(summary = "Get donation items", description = "Retrieve a list of donation items associated with the donation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    })
    public ResponseEntity<List<DonationItemReceptionDTO>> getDonationItems(@PathVariable Long id){
        return new ResponseEntity<>(this.donationService.getDonationItems(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    @PostMapping("/{id}/receive")
    @Operation(summary = "Confirm donation reception", description = "Allows an NGO to receive a donation after it has been picked up by a driver.")
    public ResponseEntity<Void> receiveDonation(@PathVariable Long id, @RequestBody ReceivedDonationDTO dto, Authentication authentication){
        String email = authentication.getName();
        this.donationService.receiveDonation(id, dto, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('NGO', 'ADMIN', 'DONOR')")
    @GetMapping("/{id}/certificate")
    @Operation(summary = "Download donation certificate", description = "Download the certificate for a specific donation")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id){
        System.out.println("Buscando certificado para donacion ID: " + id);
        byte[] pdfContent = donationService.getCertificateBytes(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Certificado_EcoNexo_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    @PostMapping("/{id}/reject-full")
    @Operation(summary = "Reject donation with details", description = "Register rejection reason, date and evidence photo")
    public ResponseEntity<Void> rejectDonationWithDetails(
            @PathVariable Long id,
            @RequestBody RejectionRequestDTO dto,
            Authentication authentication) {

        donationService.rejectDonationWithDetails(id, dto, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
