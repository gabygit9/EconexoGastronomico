package com.tfi.econexo.controller;

import com.tfi.econexo.dto.NeighborhoodLookupDTO;
import com.tfi.econexo.service.NeighborhoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/neighborhoods")
@RequiredArgsConstructor
@Tag(name = "Neighborhoods", description = "Endpoints for neighborhood management")
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    @GetMapping("/public")
    @Operation(
            summary = "Get neighborhoods",
            description = "Return a list of neighborhoods with their id and name."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of neighborhoods"),
            @ApiResponse(responseCode = "404", description = "No neighborhoods found")
    })
    public ResponseEntity<List<NeighborhoodLookupDTO>> getNeighborhoods() {
        List<NeighborhoodLookupDTO> neighborhoods = neighborhoodService.findAll().stream()
                .map(n -> new NeighborhoodLookupDTO(n.getId(), n.getName()))
                .toList();

        return ResponseEntity.ok(neighborhoods);
    }
}
