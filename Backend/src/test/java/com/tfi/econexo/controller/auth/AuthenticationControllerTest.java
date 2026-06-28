package com.tfi.econexo.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.config.AuditorAwareImpl;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dto.auth.login.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.login.AuthResponseDTO;
import com.tfi.econexo.dto.auth.logistics.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.dto.auth.logistics.VehicleRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.VehicleResponseDTO;
import com.tfi.econexo.dto.auth.ngo.NgoRegistrationDTO;
import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.logistics.VehicleType;
import com.tfi.econexo.service.logistics.DriverService;
import com.tfi.econexo.service.NeighborhoodService;
import com.tfi.econexo.service.NgoService;
import com.tfi.econexo.service.auth.*;
import com.tfi.econexo.service.impl.auth.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuditorAwareImpl.class))
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private NeighborhoodService neighborhoodService;

    @MockitoBean
    private NgoService ngoService;

    @MockitoBean
    private PermissionService permissionService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private BlacklistedTokenService blacklistedTokenService;

    @MockitoBean
    private AdminUserService adminUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginUser_success() throws Exception {
        String json = objectMapper.writeValueAsString(new AuthLoginRequestDTO("test@mail.com", "12345678"));

        when(userDetailsService.loginUser(any(AuthLoginRequestDTO.class))).thenReturn(
                new AuthResponseDTO("test@mail.com", "login successful", "token", true));

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jwt").value("token"))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    void loginUser_Unauthorized() throws Exception {
        String json = objectMapper.writeValueAsString(new AuthLoginRequestDTO("test@mail.com", "123456"));

        when(userDetailsService.loginUser(any(AuthLoginRequestDTO.class))).thenThrow(
                new BadCredentialsException("Invalid credentials")
        );

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginUser_badRequest() throws Exception {
        String json = """
                {
                 "password": "12345678"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDonor_ReturnCreated_WhenPayloadIsValid() throws Exception {
        String json = objectMapper.writeValueAsString(new DonorRegistrationDTO("test@mail.com", "12345678",
                "Hornito Santiagueño", "Hornito Alimentos SRL", "30712345678",
                "351155123456", "Av. Hipólito Yrigoyen", "450", "PB", "A",
                "RESTAURANT", -31.533333, -57.533333, 1L));

        when(authService.registerDonor(any(DonorRegistrationDTO.class)))
                .thenReturn(new DonorResponseDTO(1L, "test@mail.com",
                        "Hornito Santiagueño", "Hornito Alimentos SRL",
                        "30712345678", "351155123456", "Av. Hipólito Yrigoyen",
                        "450", "PB", "A", 1L, "PENDING"));

        mockMvc.perform(
                post("/api/v1/auth/register/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.trade_name").value("Hornito Santiagueño"))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    void registerDonor_ReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
        String json = """
                {
                 "email": "test@mail.com",
                 "password": "123456",
                 "trade_name": "Hornito Santiagueño"
                }
                """;

        mockMvc.perform(
                post("/api/v1/auth/register/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerNgo_ReturnCreated_WhenPayloadIsValid() throws Exception {
        String json = objectMapper.writeValueAsString(new NgoRegistrationDTO("Comedor Caritas Felices",
                "22589875879", "87889987","María Gómez",  "Bv. San Juan", "111",
                "1", "A", "3542343455",  1L, 40.444, 23.000,
                "test@mail.com", "12345678", "SHELTER"));

        when(authService.registerNgo(any(NgoRegistrationDTO.class))).thenReturn(
                new NgoResponseDTO(1L, "test@email.com", "Comedor Caritas Felices",
                        "87889987", "22589875879", "María Gómez",
                        "3542343455", "Bv. San Juan", "111", "1", "A", 1L, "PENDING"
                )
        );

        mockMvc.perform(
                post("/api/v1/auth/register/ngo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tax_id").value("22589875879"))
                .andExpect(jsonPath("$.legal_personality_number").value("87889987"));
    }

    @Test
    void registerNgo_ReturnConflict_WhenNgoAlreadyExists() throws Exception {
        String json = objectMapper.writeValueAsString(new NgoRegistrationDTO("Comedor Caritas Felices",
                "22589875879", "87889987","María Gómez",  "Bv. San Juan", "111",
                "1", "A", "3542343455",  1L, 40.444, 23.000,
                "test@mail.com", "12345678", "SHELTER"));

        when(authService.registerNgo(any(NgoRegistrationDTO.class)))
                .thenThrow(new ConflictException("Ngo already exists"));

        mockMvc.perform(
                post("/api/v1/auth/register/ngo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isConflict());
    }

    @Test
    void registerDriver_ReturnCreated_WhenPayloadIsValid() throws Exception {
        String json = objectMapper.writeValueAsString(new DriverRegistrationDTO("Ana",
                "Perez","235039876522", LocalDate.now().minusYears(17),
                "ana@mail.com", "Pass1234", "www.url.com", LocalDate.of(1990, 4, 12),
                new VehicleRegistrationDTO(
                        VehicleType.CAR, true, 1000, "AA123CC",
                        "www.url.com", "www.url1.com", LocalDate.of(2029, 4,12)
                ),
                "354325543223", "Obispo Trejo", "440", "PB",
                "A", -32.00393, -64.999332, 1L));

        when(authService.registerDriver(any(DriverRegistrationDTO.class))).thenReturn(
                new DriverResponseDTO(
                        1L, "Ana", "Perez", "ana@mail.com", "235039876522",
                        "344215534343", LocalDate.of(1990,5,30),
                        String.valueOf(RegistrationStatus.PENDING), LocalDate.of(2027,5,30),
                        "Obispo Trejo", "440", "PB", "A", "Nueva Córdoba",
                        List.of(new VehicleResponseDTO(1L, VehicleType.CAR, true,
                                1000, "AA123CC",null, null, LocalDate.of(2029, 4,12)))));

        mockMvc.perform(
                        post("/api/v1/auth/register/driver")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tax_id").value("235039876522"));
    }

    @Test
    void registerDriver_ReturnBadRequest_WhenPayloadIsInValid() throws Exception {
        String json = """
                {
                 "email": "test@mail.com",
                 "password": "123456",
                 "first_name": "Hernán"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register/driver")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void registerDriver_ReturnBadRequest_WhenCarHasNotNumberPlate() throws Exception {
        String json = """
                {
                 "first_name": "Ana",
                 "last_name": "Pérez",
                 "tax_id": "21351456855",
                 "email": "ana.driver@example.com",
                 "password": "Password123!",
                 "birth_date": "1990-05-15",
                 "phone_number": "351155123456",
                 "food_handler_certificate_url": "https://res.cloudinary.com/demo/image/upload/libreta_sanitaria.jpg",
                 "food_handler_certificate_expiration": "2027-12-31",
                 "street": "Av. Colón",
                 "street_number": "1500",
                 "floor": "2",
                 "apartment": "B",
                 "latitude": -31.4035,
                 "longitude": -64.1950,
                 "neighborhood_id": 1,
                 "vehicle": {
                     "vehicle_type": "CAR",
                     "has_refrigeration": true,
                     "capacity_kg": 150,
                     "number_plate": null,
                     "driver_license_front_url": "www.front.com",
                     "driver_license_back_url": "www.back.com",
                     "driver_license_expiration": "2029-02-20"
                     }
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register/driver")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDriver_ReturnConflictException_WhenEmailAlreadyExists() throws Exception {
        String json = objectMapper.writeValueAsString(new DriverRegistrationDTO("Ana",
                "Perez","235039876522", LocalDate.now().minusYears(17),
                "ana@mail.com", "Pass1234", "www.url.com", LocalDate.of(1990, 4, 12),
                new VehicleRegistrationDTO(
                        VehicleType.CAR, true, 1000, "AA123CC",
                        "www.url.com", "www.url1.com", LocalDate.of(2029, 4,12)
                ),
                "354325543223", "Obispo Trejo", "440", "PB",
                "A", -32.00393, -64.999332, 1L));

        when(authService.registerDriver(any(DriverRegistrationDTO.class))).thenThrow(new ConflictException("Driver already exists"));

        mockMvc.perform(
                        post("/api/v1/auth/register/driver")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void logout_success() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void logout_InvalidRequest_400_WhenThereAreNoHeader() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}