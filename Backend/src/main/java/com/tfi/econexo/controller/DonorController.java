package com.tfi.econexo.controller;

import com.tfi.econexo.entity.donation.DonorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/donors")
@RequiredArgsConstructor
@Tag(name = "Donors", description = "Endpoints for donor types")
public class DonorController {

    @GetMapping("/public/donor-types")
    @Operation(
            summary = "Get donor types",
            description = "Return a Donor types List with its value and label."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donor type List successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, String>>> getDonorTypes() {
        List<Map<String, String>> types = Arrays.stream(DonorType.values())
                .map(type -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("value", type.name());
                    map.put("label", type.name().charAt(0) + type.name().substring(1).toLowerCase().replace("_", " "));
                    return map;
                })
                .toList();
        return ResponseEntity.ok(types);
    }
}
