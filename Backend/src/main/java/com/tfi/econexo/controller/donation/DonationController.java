package com.tfi.econexo.controller.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.donation.catalog.CategoryDTO;
import com.tfi.econexo.dto.donation.catalog.ProductDTO;
import com.tfi.econexo.dto.donation.catalog.UnitOfMeasureDTO;
import com.tfi.econexo.service.donation.CatalogService;
import com.tfi.econexo.service.donation.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
@Tag(name = "Donations", description = "Endpoints for donations")
public class DonationController {

    private final DonationService donationService;
    private final CatalogService catalogService;

    @PostMapping("/donate")
    @Operation(summary = "Create a new donation",
            description = "Create a new donation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Donation created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
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
}
