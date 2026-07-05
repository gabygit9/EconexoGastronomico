package com.tfi.econexo.controller.donation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.config.AuditorAwareImpl;
import com.tfi.econexo.dto.donation.summary.DonationSummaryResponseDTO;
import com.tfi.econexo.dto.reception.DonationItemReceptionDTO;
import com.tfi.econexo.dto.reception.ReceivedDonationDTO;
import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.security.config.SecurityConfig;
import com.tfi.econexo.service.auth.BlacklistedTokenService;
import com.tfi.econexo.service.donation.CatalogService;
import com.tfi.econexo.service.donation.DonationService;
import com.tfi.econexo.service.donation.DonorService;
import com.tfi.econexo.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonationController.class)
@AutoConfigureMockMvc
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuditorAwareImpl.class))
@Import(SecurityConfig.class)
class DonationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean DonationService donationService;
    @MockitoBean DonorService donorService;
    @MockitoBean CatalogService catalogService;
    @MockitoBean JwtUtils jwtUtils;
    @MockitoBean UserDetailsService userDetailsService;
    @MockitoBean BlacklistedTokenService blacklistedTokenService;

    UserSec user;
    Role role;

    @BeforeEach
    void setup() {
        role = new Role();
        role.setRole("DONOR");

        user = new UserSec();
        user.setRolesList(Set.of(role));
    }

    @Test
    @WithMockUser(roles = "NGO")
    void getAvailableDonations_Success() throws Exception {

        when(donationService.getAvailableDonationsSummary()).thenReturn(List.of(new DonationSummaryResponseDTO(
                1L, "Business", LocalDateTime.now().plusDays(2),true , List.of())));

        mockMvc.perform(
                        get("/api/v1/donations/available"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DONOR")
    void getAvailableDonations_Forbidden_NoNgoRole() throws Exception {

        mockMvc.perform(
                        get("/api/v1/donations/available"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAvailableDonations_Unauthorized_NoTokenProvided ()throws Exception {

        mockMvc.perform(
                        get("/api/v1/donations/available"))
                .andExpect(status().isUnauthorized()
                );
    }

    @Test
    @WithMockUser(roles = "NGO")
    void getDonationItems_ShouldReturnList() throws Exception {
        Long donationId = 1L;
        List<DonationItemReceptionDTO> items = List.of(
                new DonationItemReceptionDTO(1L, "Lasagna", 10.0, "kg", "Congelada")
        );

        when(donationService.getDonationItems(donationId)).thenReturn(items);

        mockMvc.perform(get("/api/v1/donations/{id}/items", donationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product_name").value("Lasagna"));
    }

    @Test
    @WithMockUser(roles = "NGO")
    void receiveDonation_ShouldReturnNoContent() throws Exception {
        Long donationId = 1L;
        ReceivedDonationDTO dto = new ReceivedDonationDTO("Todo correcto");

        mockMvc.perform(post("/api/v1/donations/{id}/receive", donationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(donationService, times(1)).receiveDonation(eq(donationId), any(ReceivedDonationDTO.class));
    }
}