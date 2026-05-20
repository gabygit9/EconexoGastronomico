package com.tfi.econexo.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.config.AuditorAwareImpl;
import com.tfi.econexo.dto.auth.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.AuthResponseDTO;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private PermissionService permissionService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private UserService userService;

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
}