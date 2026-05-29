package com.tfi.econexo.controller.ngo;

import com.tfi.econexo.entity.ngo.NgoType;
import com.tfi.econexo.service.NgoService;
import com.tfi.econexo.utils.EnumUtils;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Endpoints for ngo types")
public class NgoController {

    private final NgoService ngoService;

    @GetMapping("public/ngo-types")
    @Operation(
            summary = "Get ngo types",
            description = "Return a Ngo types List with its value and label."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ngo type List successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, String>>> getNgoTypes (){
        return ResponseEntity.ok(EnumUtils.toDropdownList(NgoType.class));
    }
}
