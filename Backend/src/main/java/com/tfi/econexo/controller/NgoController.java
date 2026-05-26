package com.tfi.econexo.controller;

import com.tfi.econexo.service.NgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class NgoController {

    private final NgoService ngoService;
}
