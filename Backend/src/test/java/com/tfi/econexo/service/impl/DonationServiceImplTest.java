package com.tfi.econexo.service.impl;

import com.tfi.econexo.dto.donation.summary.DonationSummaryResponseDTO;
import com.tfi.econexo.dto.donation.item.DonationItemRequestDTO;
import com.tfi.econexo.dto.donation.item.DonationItemResponseDTO;
import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.dto.reception.ReceivedDonationDTO;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.donation.catalog.Category;
import com.tfi.econexo.model.donation.catalog.Product;
import com.tfi.econexo.model.donation.catalog.ProductType;
import com.tfi.econexo.model.donation.catalog.UnitOfMeasure;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.model.location.City;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.repository.donation.DonationItemRepository;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.catalog.ProductRepository;
import com.tfi.econexo.repository.donation.catalog.UnitOfMeasureRepository;
import com.tfi.econexo.service.donation.DonorService;
import com.tfi.econexo.service.impl.donation.DonationServiceImpl;
import com.tfi.econexo.utils.GeometryUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {

    @Mock DonationRepository donationRepository;
    @Mock ProductRepository productRepository;
    @Mock DonorService donorService;
    @Mock GeocodingService geocodingService;
    @Mock DonationMapper donationMapper;
    @Mock UnitOfMeasureRepository unitOfMeasureRepository;
    @Mock DonationItemRepository donationItemRepository;
    @InjectMocks DonationServiceImpl donationService;

    @Mock Authentication authentication;
    @Mock SecurityContext securityContext;

    Donation donation;
    DonationResponseDTO donationResponseDTO;
    DonationRequestDTO donationRequestDTO;

    DonationItem donationItem;
    DonationItemRequestDTO donationItemRequestDTO;
    DonationItemResponseDTO donationItemResponseDTO;

    Donor donor;

    Product product;
    ProductType productType;
    Category category;
    UnitOfMeasure uom;


    @BeforeEach
    void setUp(){
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("test@donor.com");
        SecurityContextHolder.setContext(securityContext);

        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName("alberdi");
        City city = new City();
        city.setName("Córdoba");
        neighborhood.setCity(city);

        uom = new UnitOfMeasure("kg");
        uom.setId(1L);

        donationItemRequestDTO = new DonationItemRequestDTO(1L, 10.00, "123456789", LocalDateTime.now(),
                LocalDateTime.now().plusDays(2), "10", "This product contains peanuts", "This product is organic", "description", uom.getId());
        donationItemResponseDTO = new DonationItemResponseDTO(1L, "Masas finas", "Panadería", "Panificados",
                10.0, "unidades", "1241", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "24", "This product contains peanuts", "This product is organic", "None");
        donationRequestDTO = new DonationRequestDTO(LocalDateTime.now(), LocalDateTime.now(), List.of(donationItemRequestDTO));
        donationResponseDTO = new DonationResponseDTO(1L, "AVAILABLE", LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), "El Hornito", "Caritas", "Av. Independencia 123", "Lima 23", 40.7128 , -40.7128, 40.7128, -40.7128, List.of(donationItemResponseDTO), null);
        donor = new Donor();
        donor.setStreet("Obispo Trejo");
        donor.setStreetNumber("123");
        donor.setNeighborhood(neighborhood);

        donation = new Donation();
        donation.setPickupStartTime(LocalDateTime.now());
        donation.setPickupEndTime(LocalDateTime.now().plusDays(2));

        productType = new ProductType();
        category = new Category();
        product = new Product("Masas finas", true, true, productType, category);

        donationItem = new DonationItem();
        donationItem.setExpirationDate(LocalDateTime.now().plusDays(2));

    }

    @Test
    void donate_WithoutPriorAddressRegistered_ShouldCreateDonation(){
        when(donorService.findByUserEmail(any())).thenReturn(Optional.of(donor));
        when(geocodingService.getCoordinates(anyString())).thenReturn(new GeocodingService.Coordinates(-31.983, -65.000));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(unitOfMeasureRepository.findById(any())).thenReturn(Optional.of(uom));
        when(donationMapper.toItemEntity(any())).thenReturn(donationItem);
        when(donationRepository.save(any())).thenReturn(donation);
        when(donationMapper.toResponseDTO(any())).thenReturn(donationResponseDTO);

        DonationResponseDTO response = donationService.donate(donationRequestDTO);

        assertNotNull(response);
        assertNotNull(donor.getLocation());
        verify(geocodingService, times(1)).getCoordinates(anyString());
        verify(donorService, times(1)).save(donor);
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void donate_WithPriorAddressRegistered_ShouldCreateDonation(){
        Point existingLocation = GeometryUtils.createPoint(-64.18, -31.42);
        donor.setLocation(existingLocation);

        when(donorService.findByUserEmail(any())).thenReturn(Optional.of(donor));
        when(unitOfMeasureRepository.findById(any())).thenReturn(Optional.of(uom));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(donationMapper.toItemEntity(any())).thenReturn(donationItem);
        when(donationRepository.save(any())).thenReturn(donation);
        when(donationMapper.toResponseDTO(any())).thenReturn(donationResponseDTO);

        DonationResponseDTO response = donationService.donate(donationRequestDTO);

        assertNotNull(response);
        verify(geocodingService, never()).getCoordinates(anyString());
        verify(donorService, never()).save(donor);
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void donate_DonorNotFound_ShouldThrowException(){
        when(donorService.findByUserEmail(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> donationService.donate(donationRequestDTO));
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void donate_ProductNotFound_ShouldThrowException(){
        when(donorService.findByUserEmail(any())).thenReturn(Optional.of(donor));
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> donationService.donate(donationRequestDTO));
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void donate_WhenGeocodingFails_ShouldCreateAndSaveDonation(){
        when(donorService.findByUserEmail(any())).thenReturn(Optional.of(donor));
        when(geocodingService.getCoordinates(anyString())).thenReturn(null);
        when(unitOfMeasureRepository.findById(any())).thenReturn(Optional.of(uom));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(donationMapper.toItemEntity(any())).thenReturn(donationItem);
        when(donationRepository.save(any())).thenReturn(donation);
        when(donationMapper.toResponseDTO(any())).thenReturn(donationResponseDTO);

        DonationResponseDTO response = donationService.donate(donationRequestDTO);

        assertNotNull(response);
        verify(donorService, never()).save(any());
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void getAvailableDonationsSummary_WhenDonationsExist_ShouldReturnSummaryDTOs(){
        DonationSummaryResponseDTO expectedSummary = new DonationSummaryResponseDTO(
                1L, "El Hornito", LocalDateTime.now().plusDays(2),true, List.of());

        when(donationItemRepository.findByDonation_StatusOrderByExpirationDateAsc(DonationStatus.AVAILABLE))
                .thenReturn(List.of(donationItem, donationItem));
        when(donationMapper.toSummaryResponseDTO(any(Donation.class))).thenReturn(expectedSummary);

        List<DonationSummaryResponseDTO> response = donationService.getAvailableDonationsSummary();

        assertEquals(2, response.size());
        verify(donationItemRepository, times(1)).findByDonation_StatusOrderByExpirationDateAsc(DonationStatus.AVAILABLE);
        verify(donationMapper, times(2)).toSummaryResponseDTO(any(Donation.class));
        assertEquals(response.get(0).businessName(), expectedSummary.businessName());
    }

    @Test
    void getAvailableDonationsSummary_WhenListIsEmpty_ShouldReturnEmptyList(){
        when(donationItemRepository.findByDonation_StatusOrderByExpirationDateAsc(DonationStatus.AVAILABLE))
                .thenReturn(List.of());

        List<DonationSummaryResponseDTO> response = donationService.getAvailableDonationsSummary();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(donationItemRepository, times(1)).findByDonation_StatusOrderByExpirationDateAsc(DonationStatus.AVAILABLE);
        verify(donationMapper, never()).toSummaryResponseDTO(any(Donation.class));
    }

    @Test
    void cancelTrip_WhenValidAssignedTrip_ShouldRollbackToRequestedAndNullifyDriver() {
        Long donationId = 1L;
        String driverEmail = "voluntario@correo.com";

        Driver mockDriver = new Driver();
        Donation mockDonation = new Donation();
        mockDonation.setId(donationId);
        mockDonation.setStatus(DonationStatus.ASSIGNED);
        mockDonation.setDriver(mockDriver);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(mockDonation));
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        donationService.cancelTrip(donationId, driverEmail);

        ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository, times(1)).save(donationCaptor.capture());

        Donation savedDonation = donationCaptor.getValue();
        assertEquals(DonationStatus.REQUESTED, savedDonation.getStatus());
        assertNull(savedDonation.getDriver());
    }

    @Test
    void cancelTrip_WhenTripIsAlreadyDelivered_ShouldThrowIllegalStateException() {
        Long donationId = 1L;
        String driverEmail = "voluntario@correo.com";

        Donation mockDonation = new Donation();
        mockDonation.setId(donationId);
        mockDonation.setStatus(DonationStatus.DELIVERED);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(mockDonation));

        assertThrows(IllegalStateException.class, () -> {
            donationService.cancelTrip(donationId, driverEmail);
        });

        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void cancelTrip_WhenDonationDoesNotExist_ShouldThrowEntityNotFoundException() {
        Long donationId = 99L;
        String driverEmail = "voluntario@correo.com";

        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            donationService.cancelTrip(donationId, driverEmail);
        });
    }

    @Test
    void receiveDonation_ShouldChangeStatusToDelivered_WhenStatusIsPendingNgo() {
        Long donationId = 1L;
        Donation donation = new Donation();
        donation.setStatus(DonationStatus.DELIVERED_PENDING_NGO);
        ReceivedDonationDTO dto = new ReceivedDonationDTO("Todo recibido correctamente", List.of(), true, "");

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        donationService.receiveDonation(donationId, dto, anyString());

        assertEquals(DonationStatus.DELIVERED, donation.getStatus());
        assertEquals("Todo recibido correctamente", donation.getReceptionComments());
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void receiveDonation_ShouldThrowException_WhenStatusIsNotPendingNgo() {
        Long donationId = 1L;
        Donation donation = new Donation();
        donation.setStatus(DonationStatus.ASSIGNED);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        assertThrows(IllegalStateException.class, () -> donationService.receiveDonation(donationId, new ReceivedDonationDTO("", List.of(), true, ""), anyString()));
    }
}