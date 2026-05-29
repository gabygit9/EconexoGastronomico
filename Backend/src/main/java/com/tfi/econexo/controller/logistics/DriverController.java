package com.tfi.econexo.controller.logistics;

import com.tfi.econexo.service.DriverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Endpoints for drivers")
public class DriverController {

    private final DriverService driverService;
}
