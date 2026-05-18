package com.tfi.econexo.controller.auth;

import com.tfi.econexo.entity.security.Permission;
import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.service.auth.PermissionService;
import com.tfi.econexo.service.auth.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.findById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE')")
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
    public ResponseEntity<Role> updateRole(@RequestBody Role role) {
        Role updatedRole = roleService.save(role);
        return ResponseEntity.ok(updatedRole);
    }

}
