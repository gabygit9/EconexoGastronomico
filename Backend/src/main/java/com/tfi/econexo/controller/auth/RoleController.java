package com.tfi.econexo.controller.auth;

import com.tfi.econexo.model.auth.Permission;
import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.service.auth.PermissionService;
import com.tfi.econexo.service.auth.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Roles management")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Get all roles. Only admins can do this")
    @ApiResponse(responseCode = "200", description = "List of Roles found")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by id", description = "Get role by id. Only admins can do this")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found", content = {@Content(schema = @Schema(implementation = Role.class))}),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.findById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE')")
    @Operation(summary = "Create new Role", description = "Create a new role. Only admins with CREATE authority can do this")
    @ApiResponse(responseCode = "200", description = "Role created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))})
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Set<Permission> permissionList = new HashSet<>();
        Permission readPermission;

        for(Permission per : role.getPermissionsList()){
            readPermission = permissionService.findById(per.getId()).orElse(null);
            if(readPermission != null){
                permissionList.add(readPermission);
            }
        }

        role.setPermissionsList(permissionList);
        Role newRole = roleService.save(role);
        return ResponseEntity.ok(newRole);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Role", description = "Update a role. Only admins can do this")
    @ApiResponse(responseCode = "200", description = "Role updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))})
    public ResponseEntity<Role> updateRole(@RequestBody Role role) {
        Role updatedRole = roleService.save(role);
        return ResponseEntity.ok(updatedRole);
    }

}
