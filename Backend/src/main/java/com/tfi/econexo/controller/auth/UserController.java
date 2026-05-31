package com.tfi.econexo.controller.auth;

import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.service.auth.RoleService;
import com.tfi.econexo.service.auth.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints to manage users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Get a list of all users. Only admins can do this")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public ResponseEntity<List<UserSec>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by id", description = "Get a user by id. Only admins can do this")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserSec.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserSec> getUserById(@PathVariable Long id) {
        Optional<UserSec> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new User", description = "Create a new user. Only admins can do this")
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200", description = "User created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserSec.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    public ResponseEntity<UserSec> createUser(@RequestBody UserSec userSec) {
        Set<Role> roleList = new HashSet<>();
        Role readRole;

        //Encriptar contrasena
        userSec.setPassword(userService.encryptPassword(userSec.getPassword()));

        for(Role role : userSec.getRolesList()) {
            readRole = roleService.findById(role.getId()).orElse(null);
            if(readRole != null){
                roleList.add(readRole);
            }
        }

        if(!roleList.isEmpty()){
            userSec.setRolesList(roleList);
            UserSec newUser = userService.save(userSec);
            return ResponseEntity.ok(newUser);
        }
        return ResponseEntity.badRequest().build();
    }
}