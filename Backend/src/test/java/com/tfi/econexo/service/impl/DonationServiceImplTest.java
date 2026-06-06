package com.tfi.econexo.service.impl;

import com.tfi.econexo.dto.donation.DonationItemRequestDTO;
import com.tfi.econexo.dto.donation.DonationItemResponseDTO;
import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.donation.catalog.Category;
import com.tfi.econexo.model.donation.catalog.Product;
import com.tfi.econexo.model.donation.catalog.ProductType;
import com.tfi.econexo.model.donation.catalog.UnitOfMeasure;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.location.City;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.catalog.ProductRepository;
import com.tfi.econexo.repository.donation.catalog.UnitOfMeasureRepository;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.utils.GeometryUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@donor.com");
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
                10.0, "unidades", "1241", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "24", "This product contains peanuts", "This product is organic");
        donationRequestDTO = new DonationRequestDTO(LocalDateTime.now(), LocalDateTime.now(), List.of(donationItemRequestDTO));
        donationResponseDTO = new DonationResponseDTO(1L, "AVAILABLE", LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), "El Hornito", List.of(donationItemResponseDTO));
        donor = new Donor();
        donor.setStreet("Obispo Trejo");
        donor.setStreetNumber("123");
        donor.setNeighborhood(neighborhood);

        donation = new Donation();
        donation.setPickupStartTime(LocalDateTime.now());
        donation.setPickupEndTime(LocalDateTime.now().plusDays(2));

        donationItem = new DonationItem();
        donationItem.setExpirationDate(LocalDateTime.now().plusDays(2));

        productType = new ProductType();
        category = new Category();
        product = new Product("Masas finas", true, true, productType, category);
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
}