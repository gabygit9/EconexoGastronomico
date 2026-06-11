package com.tfi.econexo.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfi.econexo.config.AuditorAwareImpl;
import com.tfi.econexo.dto.auth.admin.UpdateStatusRequestDTO;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.security.config.SecurityConfig;
import com.tfi.econexo.service.auth.*;
import com.tfi.econexo.service.impl.auth.UserDetailsServiceImpl;
import com.tfi.econexo.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuditorAwareImpl.class))
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AdminUserService adminUserService;
    @MockitoBean JwtUtils jwtUtils;
    @MockitoBean UserDetailsServiceImpl userDetailsServiceImpl;
    @MockitoBean BlacklistedTokenService blacklistedTokenService;
    @MockitoBean AuthService authService;
    @MockitoBean PermissionService permissionService;
    @MockitoBean RoleService roleService;
    @MockitoBean UserService userService;


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_ShouldReturn200_OK() throws Exception {

        when(adminUserService.getAllRegisteredUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DONOR")
    public void testGetAllUsers_ShouldReturn403_FORBIDDEN() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserStatus_ShouldReturn204_NoContent() throws Exception {
        String json = objectMapper.writeValueAsString(new UpdateStatusRequestDTO(RegistrationStatus.APPROVED));

        mockMvc.perform(patch("/api/v1/admin/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());

        verify(adminUserService, times(1)).updateUserStatus(1L, RegistrationStatus.APPROVED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserStatus_WhenStatusDoesntExist_ShouldReturn400_BadRequest() throws  Exception {
        String json = "{'status': 'ESTADO_FALSO'}";

        mockMvc.perform(patch("/api/v1/admin/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}