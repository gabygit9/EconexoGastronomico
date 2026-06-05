package com.tfi.econexo.controller.donation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.dto.donation.DonationItemRequestDTO;
import com.tfi.econexo.dto.donation.DonationRequestDTO;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.model.donation.catalog.Product;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.location.City;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.donation.DonorRepository;
import com.tfi.econexo.repository.donation.catalog.ProductRepository;
import com.tfi.econexo.repository.location.CityRepository;
import com.tfi.econexo.repository.location.NeighborhoodRepository;
import com.tfi.econexo.service.impl.GeocodingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class DonationControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private DonorRepository donorRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private NeighborhoodRepository neighborhoodRepository;
    @Autowired private CityRepository cityRepository;

    @MockitoBean private GeocodingService geocodingService;

    private Long productId;

    @BeforeEach
    void setUp(){
        UserSec testUser = new UserSec();
        testUser.setEmail("test@donor.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        City city = new City();
        city.setName("test");
        city = cityRepository.save(city);

        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName("test");
        neighborhood.setCity(city);
        neighborhood = neighborhoodRepository.save(neighborhood);

        Donor testDonor = new Donor();
        testDonor.setUser(testUser);
        testDonor.setTradeName("test");
        testDonor.setLegalName("test");
        testDonor.setTaxId("123456789");
        testDonor.setStreet("test");
        testDonor.setStreetNumber("123");
        testDonor.setNeighborhood(neighborhood);
        donorRepository.save(testDonor);

        Product product = productRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productId = product.getId();
    }

    @Test
    @WithMockUser(username = "test@donor.com", roles = "DONOR")
    void donate_ValidRequest_ShouldReturn201Created() throws Exception{
        DonationItemRequestDTO itemDto = new DonationItemRequestDTO(productId, 10.00,
                "LOTE-123", LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                "10", "Ninguna", "Test");
        DonationRequestDTO requestDTO = new DonationRequestDTO(LocalDateTime.now(),
                LocalDateTime.now().plusHours(3), List.of(itemDto));

        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        when(geocodingService.getCoordinates(anyString())).thenReturn(new GeocodingService.Coordinates(-31.42, -64.18));

        mockMvc.perform(post("/api/v1/donations/donate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.business_name").value("test"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "test@donor.com", roles = "DONOR")
    void donate_InvalidRequest_ShouldReturn400BadRequest() throws Exception{
        DonationItemRequestDTO invalidItemDto = new DonationItemRequestDTO(null, 10.00,
                "LOTE-MALO", LocalDateTime.now(), LocalDateTime.now().minusDays(5),
                "10","Ninguna", "Test");
        DonationRequestDTO invalidRequestDTO = new DonationRequestDTO(LocalDateTime.now(),
                LocalDateTime.now().plusHours(3), List.of(invalidItemDto));

        String jsonBody = objectMapper.writeValueAsString(invalidRequestDTO);

        mockMvc.perform(post("/api/v1/donations/donate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

}