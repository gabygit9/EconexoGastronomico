package com.tfi.econexo.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.config.AuditorAwareImpl;
import com.tfi.econexo.dto.auth.login.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.login.AuthResponseDTO;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.service.auth.AuthService;
import com.tfi.econexo.service.auth.PermissionService;
import com.tfi.econexo.service.auth.RoleService;
import com.tfi.econexo.service.auth.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationControllerTest.class)
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuditorAwareImpl.class))
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private PermissionService permissionService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginUser_success() throws Exception {
        String json = objectMapper.writeValueAsString(new AuthLoginRequestDTO("test@mail.com", "123456"));

        when(userDetailsService.loginUser(any(AuthLoginRequestDTO.class))).thenReturn(
                new AuthResponseDTO("test@mail.com", "login successful", "token", true));

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
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
                 "password": "123456"
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
                        "450", "PB", "A", 1L));

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
}