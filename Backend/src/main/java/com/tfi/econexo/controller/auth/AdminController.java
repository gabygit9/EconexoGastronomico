package com.tfi.econexo.controller.auth;

import com.tfi.econexo.dto.auth.admin.UpdateStatusRequestDTO;
import com.tfi.econexo.dto.auth.admin.UserAdminResponseDTO;
import com.tfi.econexo.service.auth.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Users", description = "Endpoints to Admin Users only")
public class AdminController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Get all registered users", description = "Get all registered users")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all registered users"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<UserAdminResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllRegisteredUsers());
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Update user status", description = "Update user status")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId, @RequestBody UpdateStatusRequestDTO request) {
        adminUserService.updateUserStatus(userId, request.status());
        return ResponseEntity.noContent().build();
    }
}
