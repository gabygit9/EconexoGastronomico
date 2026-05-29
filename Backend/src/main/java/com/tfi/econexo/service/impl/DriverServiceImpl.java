package com.tfi.econexo.service.impl;

import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
}
