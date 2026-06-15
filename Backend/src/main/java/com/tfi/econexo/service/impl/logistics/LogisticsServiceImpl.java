package com.tfi.econexo.service.impl.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.service.donation.DonationService;
import com.tfi.econexo.service.logistics.DriverService;
import com.tfi.econexo.service.logistics.LogisticsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl implements LogisticsService {

    private final DriverService driverService;
    private final DonationService donationService;
    private final DonationMapper donationMapper;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public List<DonationResponseDTO> getAvailableTripsNearby(String driverEmail, Double latitude, Double longitude) {

        Driver driver = driverService.findEntityByEmail(driverEmail)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        Point driverLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        List<Donation> availableDonations = donationService.findAvailableTripsNearby(driverLocation, driver.getId(), DonationStatus.REQUESTED);

        return availableDonations.stream().map(donationMapper::toResponseDTO).collect(Collectors.toList());
    }
}
