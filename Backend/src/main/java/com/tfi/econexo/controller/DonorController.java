package com.tfi.econexo.controller;

import com.tfi.econexo.entity.donation.DonorType;
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
public class DonorController {

    @GetMapping("/public/donor-types")
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
