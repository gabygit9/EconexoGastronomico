package com.tfi.econexo.service.impl;

import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.dto.donation.DonationResponseDTO;
import com.tfi.econexo.mappers.DonationMapper;
import com.tfi.econexo.model.donation.Donation;
import com.tfi.econexo.model.donation.DonationItem;
import com.tfi.econexo.model.donation.catalog.Product;
import com.tfi.econexo.model.donation.catalog.UnitOfMeasure;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.DonationStatus;
import com.tfi.econexo.repository.donation.DonationRepository;
import com.tfi.econexo.repository.donation.catalog.ProductRepository;
import com.tfi.econexo.repository.donation.catalog.UnitOfMeasureRepository;
import com.tfi.econexo.service.DonationService;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.utils.GeometryUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final GeocodingService geocodingService;
    private final DonorService donorService;
    private final ProductRepository productRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

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
}
