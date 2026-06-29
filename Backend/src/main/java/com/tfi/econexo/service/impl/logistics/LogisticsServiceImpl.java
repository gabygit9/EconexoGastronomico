package com.tfi.econexo.service.impl.logistics;

import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.logistics.DriverDeliveryEvidenceDTO;
import com.tfi.econexo.exception.TripNotAvailableException;
import com.tfi.econexo.exception.VehicleIncompatibleException;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.logistics.DeliveryEvidence;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.logistics.Vehicle;
import com.tfi.econexo.repository.logistics.DeliverEvidenceRepository;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl implements LogisticsService {

    private final DriverService driverService;
    private final DonationService donationService;
    private final DeliverEvidenceRepository deliverEvidenceRepository;
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

    @Override
    public DonationResponseDTO getTripDetailsById(Long id){
        Donation donation = donationService.findByIdDonation(id)
                .orElseThrow(() -> new EntityNotFoundException("Request trip not found."));
        return donationMapper.toResponseDTO(donation);
    }

    @Override
    @Transactional
    public void updateTripStatus(Long tripId, String newStatus, String driverEmail) {
        Donation donation = donationService.findByIdDonation(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Request trip not found."));

        if(donation.getDriver() == null){
            throw new EntityNotFoundException("The trip is not assigned to a driver.");
        }

        if(!donation.getDriver().getUser().getEmail().equals(driverEmail)){
            throw new AccessDeniedException("You are not authorized to update this trip.");
        }

        DonationStatus requestedStatus = DonationStatus.valueOf(newStatus);

        if(donation.getStatus() == DonationStatus.ASSIGNED && requestedStatus != DonationStatus.IN_TRANSIT){
            throw new IllegalArgumentException("The trip must be IN_TRANSIT before being finished.");
        }

        if(donation.getStatus() == DonationStatus.IN_TRANSIT && requestedStatus != DonationStatus.DELIVERED_PENDING_NGO){
            throw new IllegalArgumentException("An IN_TRANSIT trip can only be DELIVERED_PENDING_NGO.");
        }

        if(donation.getStatus() == DonationStatus.DELIVERED){
            if(donation.getDeliveryEvidence() == null || donation.getDeliveryEvidence().getNgoSignatureUrl() == null){
                throw new IllegalArgumentException("Can't mark as DELIVERED without NGO signature.");
            }
            throw new IllegalArgumentException("An DELIVERED trip cannot be modified.");
        }

        donation.setStatus(requestedStatus);
    }

    @Transactional
    @Override
    public void registerDriverDelivery(Long tripId, DriverDeliveryEvidenceDTO dto, String driverEmail) {
        Donation donation = donationService.findByIdDonation(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found."));

        if(!donation.getDriver().getUser().getEmail().equals(driverEmail)){
            throw new AccessDeniedException("You are not authorized to update this trip.");
        }

        DeliveryEvidence evidence = deliverEvidenceRepository.findByDonationId(tripId)
                .orElse(new DeliveryEvidence());

        evidence.setDonation(donation);
        evidence.setTemperature(dto.temperature());
        evidence.setEvidencePhotoUrl(dto.evidencePhotoUrl());
        evidence.setDriverSignatureUrl(dto.driverSignatureUrl());

        deliverEvidenceRepository.save(evidence);

        donation.setStatus(DonationStatus.DELIVERED_PENDING_NGO);
        donationService.save(donation);
    }
}
