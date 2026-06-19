package com.tfi.econexo.controller.logistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.config.AuditorAwareImpl;
import com.tfi.econexo.dto.logistics.AcceptTripRequestDTO;
import com.tfi.econexo.exception.GlobalExceptionHandler;
import com.tfi.econexo.security.config.SecurityConfig;
import com.tfi.econexo.service.auth.BlacklistedTokenService;
import com.tfi.econexo.service.logistics.DriverService;
import com.tfi.econexo.service.logistics.LogisticsService;
import com.tfi.econexo.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogisticsController.class)
@AutoConfigureMockMvc
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuditorAwareImpl.class))
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class LogisticsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private LogisticsService logisticsService;
    @MockitoBean private DriverService driverService;
    @MockitoBean JwtUtils jwtUtils;
    @MockitoBean UserDetailsService userDetailsService;
    @MockitoBean BlacklistedTokenService blacklistedTokenService;

    @Test
    @WithMockUser(username = "driver@example.com", roles = "DRIVER")
    public void acceptTrip_ConcurrencyConflict_Returns409() throws Exception{

        doThrow(new ObjectOptimisticLockingFailureException("Donation", 1L))
                .when(logisticsService)
                .acceptTrip(anyLong(),anyString(), anyLong());

        AcceptTripRequestDTO request = new AcceptTripRequestDTO(10L);

        mockMvc.perform(post("/api/v1/logistics/trips/1/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isConflict());
    }



}