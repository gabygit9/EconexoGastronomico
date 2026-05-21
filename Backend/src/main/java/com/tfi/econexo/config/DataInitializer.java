package com.tfi.econexo.config;

import com.tfi.econexo.entity.location.City;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.repository.auth.RoleRepository;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.location.CityRepository;
import com.tfi.econexo.repository.location.NeighborhoodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CityRepository cityRepository;
    private final NeighborhoodRepository neighborhoodRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role adminRole = getOrCreateRole("ADMIN");
        Role donorRole = getOrCreateRole("DONOR");
        Role ngoRole = getOrCreateRole("NGO");
        Role driverRole = getOrCreateRole("DRIVER");

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

        if(cityRepository.count() == 0){
            City cordoba = new City();
            cordoba.setName("Córdoba");
            cordoba = cityRepository.save(cordoba);

            Neighborhood nvaCba = new Neighborhood();
            nvaCba.setName("Nueva Córdoba");
            nvaCba.setCity(cordoba);

            Neighborhood gralPaz = new  Neighborhood();
            gralPaz.setName("General Paz");
            gralPaz.setCity(cordoba);

            neighborhoodRepository.saveAll(List.of(nvaCba, gralPaz));
            System.out.println("[DataInitializer] Ciudades y Barrios base creados con éxito.");
        }
    }

    private Role getOrCreateRole(String roleName){
        return roleRepository.findByRole(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRole(roleName);
                    return roleRepository.save(newRole);
                });
    }
}
