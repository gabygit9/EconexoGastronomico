package com.tfi.econexo.controller;

import com.tfi.econexo.dto.NeighborhoodLookupDTO;
import com.tfi.econexo.service.NeighborhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    @GetMapping("/public")
    public ResponseEntity<List<NeighborhoodLookupDTO>> getNeighborhoods() {
        List<NeighborhoodLookupDTO> neighborhoods = neighborhoodService.findAll().stream()
                .map(n -> new NeighborhoodLookupDTO(n.getId(), n.getName()))
                .toList();

        return ResponseEntity.ok(neighborhoods);
    }
}
