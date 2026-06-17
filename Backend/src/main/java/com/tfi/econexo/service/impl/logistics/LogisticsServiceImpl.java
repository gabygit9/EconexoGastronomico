package com.tfi.econexo.service.impl.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.exception.TripNotAvailableException;
import com.tfi.econexo.exception.VehicleIncompatibleException;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.logistics.Vehicle;
import com.tfi.econexo.service.donation.DonationService;
import com.tfi.econexo.service.logistics.DriverService;
import com.tfi.econexo.service.logistics.LogisticsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    @Transactional
    @Override
    public void acceptTrip(Long donationId, String driverEmail, Long vehicleId) {
        Donation donation = donationService.findByIdDonation(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if (donation.getStatus() != DonationStatus.REQUESTED) {
            throw new TripNotAvailableException("The trip is not available anymore. It was assigned to other entity or canceled.");
        }

        Driver driver = driverService.findEntityByEmail(driverEmail)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found."));

        Vehicle selectedVehicle = driver.getVehicles().stream()
                .filter(v -> v.getId().equals(vehicleId))
                .findFirst()
                .orElseThrow(() -> new VehicleIncompatibleException("The selected vehicle does not belong to your driver profile."));

        //Revalidar capacidad y frío
        double totalWeight = donation.getDonationItems().stream().mapToDouble(DonationItem::getQuantity).sum();
        if (selectedVehicle.getCapacityKg() < totalWeight) {
            throw new VehicleIncompatibleException("The selected vehicle does not count with enough capacity.");
        }
        if (donation.isAnyItemRefrigerated() && !selectedVehicle.isHasRefrigeration()) {
            throw new VehicleIncompatibleException("The donation requires cold chain and the vehicle does not have refrigeration.");
        }

        donation.setDriver(driver);
        donation.setVehicle(selectedVehicle);
        donation.setStatus(DonationStatus.ASSIGNED);

        donationService.save(donation);
    }
}
