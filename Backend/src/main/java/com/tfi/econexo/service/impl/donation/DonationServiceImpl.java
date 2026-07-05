package com.tfi.econexo.service.impl.donation;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.reception.DonationItemReceptionDTO;
import com.tfi.econexo.dto.reception.ReceivedDonationDTO;
import com.tfi.econexo.dto.donation.summary.DonationSummaryResponseDTO;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.donation.ReceivedItem;
import com.tfi.econexo.model.donation.ReceptionRecord;
import com.tfi.econexo.model.donation.catalog.Product;
import com.tfi.econexo.model.donation.catalog.UnitOfMeasure;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.ngo.Ngo;
import com.tfi.econexo.repository.donation.DonationItemRepository;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.ReceptionRecordRepository;
import com.tfi.econexo.repository.donation.catalog.ProductRepository;
import com.tfi.econexo.repository.donation.catalog.UnitOfMeasureRepository;
import com.tfi.econexo.repository.ngo.NgoRepository;
import com.tfi.econexo.service.donation.DonationService;
import com.tfi.econexo.service.donation.DonorService;
import com.tfi.econexo.service.impl.GeocodingService;
import com.tfi.econexo.utils.GeometryUtils;
import com.tfi.econexo.utils.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonationItemRepository donationItemRepository;
    private final ReceptionRecordRepository receptionRecordRepository;
    private final GeocodingService geocodingService;
    private final DonorService donorService;
    private final ProductRepository productRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final NgoRepository ngoRepository;
    private final NotificationService notificationService;

    private final DonationMapper donationMapper;


    @Transactional
    @Override
    public DonationResponseDTO donate(DonationRequestDTO donationRequestDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Donor donor = donorService.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Donor not found"));

        //Consumir el GeocodingService combinando la calle y número para obtener las coordenadas.
        if(donor.getLocation() == null){
            String fullAddress = donor.getStreet() + " " + donor.getStreetNumber() + ", " + donor.getNeighborhood().getName() + ", " + donor.getNeighborhood().getCity().getName();
            GeocodingService.Coordinates coords = geocodingService.getCoordinates(fullAddress);

            if(coords != null){
                Point locationPoint = GeometryUtils.createPoint(coords.lng(), coords.lat());
                donor.setLocation(locationPoint);
                donorService.save(donor);
            }
        }

        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setPickupStartTime(donationRequestDTO.pickupStartTime());
        donation.setPickupEndTime(donationRequestDTO.pickupEndTime());
        donation.setStatus(DonationStatus.AVAILABLE);
        List<DonationItem> items = donationRequestDTO.items().stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            UnitOfMeasure uom =unitOfMeasureRepository.findById(itemDto.unitOfMeasureId())
                    .orElseThrow(() -> new EntityNotFoundException("Unit of measure not found"));

            DonationItem item = donationMapper.toItemEntity(itemDto);
            item.setDonation(donation);
            item.setProduct(product);
            item.setUnitOfMeasure(uom);
            item.setDescription(itemDto.description());

            return  item;
        }).toList();

        donation.setDonationItems(items);
        Donation savedDonation = donationRepository.save(donation);

        return donationMapper.toResponseDTO(savedDonation);
    }

    @Override
    public List<DonationSummaryResponseDTO> getAvailableDonationsSummary() {
        List<Donation> donations = donationRepository.findByStatusAvailableAndNotExpired();

        return donations.stream()
                .map(donationMapper::toSummaryResponseDTO)
                .toList();
    }

    @Transactional
    @Override
    public void requestDonation(Long donationId, String ngoEmail) {
        Ngo ngo = ngoRepository.findByUser_Email(ngoEmail)
                .orElseThrow(() -> new EntityNotFoundException("Ngo not found"));

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if(donation.getStatus() != DonationStatus.AVAILABLE){
            throw new ConflictException("This donation was already requested by another NGO or it's not available anymore.");
        }

        donation.setStatus(DonationStatus.REQUESTED);
        donation.setNgo(ngo);
        donationRepository.save(donation);
    }

    @Override
    public List<Donation> findAvailableTripsNearby(Point driverLocation, Long driverId, DonationStatus status) {
        return donationRepository.findAvailableTripsNearby(driverLocation, driverId, status);
    }

    @Override
    public Optional<Donation> findByIdDonation(Long id) {
        return donationRepository.findById(id);
    }

    @Override
    public Donation save(Donation donation) {
        return donationRepository.save(donation);
    }

    @Override
    public List<DonationResponseDTO> getMyDonations(String email) {
        List<Donation> myDonations = donationRepository.findMyDonationsOrderByCreatedDateDesc(email);
        return myDonations.stream()
                .map(donationMapper::toResponseDTO)
                .toList();
    }

    @Override
    public DonationResponseDTO getDonation(Long id) {
        return donationMapper.toResponseDTO(donationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found")));
    }

    @Transactional
    @Override
    public void cancelTrip(Long donationId, String driverEmail) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if(donation.getStatus() != DonationStatus.ASSIGNED){
            throw new IllegalStateException("The trip can not be canceled if it's ASSIGEND. Current state: " + donation.getStatus());
        }

        notificationService.notifyUser(donation.getDonor().getUser().getEmail(), "El conductor ha cancelado el retiro. La donación vuelve a estar disponible para ser tomada por otro voluntario.", "Viaje cancelado por el conductor");

        if(donation.getNgo() != null && donation.getNgo().getUser() != null){
            notificationService.notifyUser(donation.getNgo().getUser().getEmail(), "El conductor asignado ha cancelado el servicio. Estamos buscando un nuevo voluntario para tu donación.", "Actualización en tu donación");
        }

        donation.setStatus(DonationStatus.REQUESTED);
        donation.setDriver(null);
        donationRepository.save(donation);
    }

    @Override
    public void rejectDonationByDriver(Long donationId, String driverEmail) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if(donation.getStatus() != DonationStatus.ASSIGNED){
            throw new IllegalStateException("Only trips ASSIGNED can be rejected. Current state: " + donation.getStatus());
        }

        notificationService.notifyUser(donation.getDonor().getUser().getEmail(), "El conductor ha reportado un problema con la mercadería y no pudo retirar la donación. La donación ha sido retirada de la red.", "Donación rechazada por el conductor");

        if(donation.getNgo() != null && donation.getNgo().getUser() != null){
            notificationService.notifyUser(donation.getNgo().getUser().getEmail(), "La donación que esperabas fue rechazada por el conductor debido a un problema con la mercadería.", "Donación rechazada por el conductor");
        }

        donation.setStatus(DonationStatus.REJECTED);
        donation.setDriver(null);
        donationRepository.save(donation);
    }

    @Override
    public void cancelDonationByDonor(Long donationId, String donorEmail) {

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if(!donation.getDonor().getUser().getEmail().equals(donorEmail)){
            throw new AccessDeniedException("You are not allowed to cancel this donation");
        }

        List<DonationStatus> allowedStatuses = List.of(DonationStatus.AVAILABLE, DonationStatus.REQUESTED, DonationStatus.ASSIGNED);

        if(!allowedStatuses.contains(donation.getStatus())) {
            throw new IllegalStateException("Only active donations can be canceled. Current state: " + donation.getStatus());
        }

        if(donation.getDriver() != null && donation.getDriver().getUser() != null && donation.getStatus() == DonationStatus.ASSIGNED){
            notificationService.notifyUser(donation.getDriver().getUser().getEmail(), "Tu viaje hacia el donante ha sido cancelado.", "Viaje Cancelado");
        } else if(donation.getNgo() != null && donation.getNgo().getUser() != null &&donation.getStatus() == DonationStatus.REQUESTED){
            notificationService.notifyUser(donation.getNgo().getUser().getEmail(), "La donación que solicitaste fue cancelada por el donante.", "Donación Cancelada");
        }

        donation.setStatus(DonationStatus.CANCELED);
        donationRepository.save(donation);
    }

    @Override
    public void rejectDriverByDonor(Long donationId, String donorEmail) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if(!donation.getDonor().getUser().getEmail().equals(donorEmail)){
            throw new AccessDeniedException("You are not allowed to cancel this donation");
        }

        if(donation.getStatus() != DonationStatus.ASSIGNED) {
            throw new IllegalStateException("Only ASSIGNED donations can be rejected by the donor");
        }

        if(donation.getDriver() != null && donation.getDriver().getUser() != null){
            notificationService.notifyUser(donation.getDriver().getUser().getEmail(), "El donante ha rechazado la asignación de este viaje. La donación vuelve a la red.", "Viaje Rechazado");
        }

        if(donation.getNgo() != null && donation.getNgo().getUser() != null){
            notificationService.notifyUser(donation.getNgo().getUser().getEmail(), "El donante ha rechazado al conductor asignado. Se está buscando uno nuevo para tu donación.", "Cambio en la logística de tu donación");
        }

        donation.setDriver(null);
        donation.setStatus(DonationStatus.REQUESTED);
        donationRepository.save(donation);
    }

    @Transactional
    @Override
    public void cancelDonationByNgo(Long donationId, String ngoEmail) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
        if(!donation.getNgo().getUser().getEmail().equals(ngoEmail)){
            throw new AccessDeniedException("You are not allowed to cancel this donation");
        }
        if(donation.getStatus() != DonationStatus.REQUESTED){
            throw new IllegalStateException("Only REQUESTED donations can be canceled by the NGO");
        }

        notificationService.notifyUser(donation.getDonor().getUser().getEmail(), "La ONG ha cancelado su solicitud. La donación vuelve a estado DISPONIBLE.", "Solicitud cancelada por la ONG");

        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setNgo(null);
        donationRepository.save(donation);
    }

    @Transactional
    @Override
    public void receiveDonation(Long donationId, ReceivedDonationDTO dto, String email) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));

        if(donation.getStatus() != DonationStatus.DELIVERED_PENDING_NGO){
            throw new IllegalStateException("Only DELIVERED_PENDING_NGO donations can be received");
        }

        if(dto.comments() != null){
            donation.setReceptionComments(dto.comments());
        }

        ReceptionRecord record = new ReceptionRecord();
        record.setDonation(donation);

        List<ReceivedItem> receivedItems = dto.receivedItems().stream().map(dtoItem -> {
            DonationItem originalItem = donationItemRepository.findById(dtoItem.itemId())
                    .orElseThrow(()-> new EntityNotFoundException("Item not found"));
            ReceivedItem received = new ReceivedItem();
            received.setDonationItem(originalItem);
            received.setReceivedQuantity(dtoItem.receivedQuantity());
            return received;
        }).toList();

        record.setItems(receivedItems);
        record.setAcceptedDisclaimer(dto.acceptedDisclaimer());
        record.setSignatureUrl(dto.signatureUrl());
        record.setAcceptanceTimestamp(LocalDateTime.now());
        record.setReceivedByEmail(email);

        receptionRecordRepository.save(record);

        donation.setStatus(DonationStatus.DELIVERED);
        donationRepository.save(donation);
    }

    @Override
    public List<DonationItemReceptionDTO> getDonationItems(Long id){
        Donation donation = this.donationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation not found"));
        return donation.getDonationItems().stream()
                .map(item -> new DonationItemReceptionDTO(
                        item.getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitOfMeasure().getDescription(),
                        item.getDescription()))
                .toList();
    }

}
