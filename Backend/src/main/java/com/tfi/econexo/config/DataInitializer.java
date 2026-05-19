package com.tfi.econexo.config;

import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.repository.auth.RoleRepository;
import com.tfi.econexo.repository.auth.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByRole("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRole("ADMIN");
                    return roleRepository.save(newRole);
                });
        String adminEmail = "admin@econexo.com";
        if(userRepository.findUserEntityByEmail(adminEmail).isEmpty()){
            UserSec adminUser = new UserSec();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode("admin1234"));
            adminUser.setEnabled(true);
            adminUser.setAccountNonExpired(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setCredentialNonExpired(true);
            adminUser.setRolesList(Set.of(adminRole));
            userRepository.save(adminUser);

            System.out.println("[DataInitializer] Usuario ADMIN creado con éxito: admin@econexo.com / admin1234");
        } else {
            System.out.println("[DataInitializer] El usuario ADMIN ya existe en la base de datos. Saltando inicialización.");
        }
    }
}
