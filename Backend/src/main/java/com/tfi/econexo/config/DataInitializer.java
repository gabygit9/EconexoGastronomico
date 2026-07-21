package com.tfi.econexo.config;

import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.model.donation.catalog.Category;
import com.tfi.econexo.model.donation.catalog.Product;
import com.tfi.econexo.model.donation.catalog.ProductType;
import com.tfi.econexo.model.donation.catalog.UnitOfMeasure;
import com.tfi.econexo.model.location.City;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.repository.auth.RoleRepository;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.donation.catalog.CategoryRepository;
import com.tfi.econexo.repository.donation.catalog.ProductRepository;
import com.tfi.econexo.repository.donation.catalog.ProductTypeRepository;
import com.tfi.econexo.repository.donation.catalog.UnitOfMeasureRepository;
import com.tfi.econexo.repository.location.CityRepository;
import com.tfi.econexo.repository.location.NeighborhoodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CityRepository cityRepository;
    private final NeighborhoodRepository neighborhoodRepository;

    private final CategoryRepository categoryRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductRepository productRepository;

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

            logger.info("Admin account has been created");
        } else {
            logger.info("User with email {} already exists", adminEmail);
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

            Neighborhood centro = new Neighborhood();
            centro.setName("Centro");
            centro.setCity(cordoba);

            Neighborhood altaCordoba = new Neighborhood();
            altaCordoba.setName("Alta Córdoba");
            altaCordoba.setCity(cordoba);

            neighborhoodRepository.saveAll(List.of(nvaCba, gralPaz, centro, altaCordoba));
            logger.info("All neighborhoods have been created");
        }

        if (productRepository.count() == 0) {
            UnitOfMeasure kg = new UnitOfMeasure(); kg.setDescription("Kilogramos");
            UnitOfMeasure un = new UnitOfMeasure(); un.setDescription("Unidades");
            UnitOfMeasure lt = new UnitOfMeasure(); lt.setDescription("Litros");
            UnitOfMeasure portion = new UnitOfMeasure(); portion.setDescription("Porciones");

            unitOfMeasureRepository.saveAll(List.of(kg, un, lt, portion));

            ProductType perishable = new ProductType(); perishable.setDescription("Perecedero");
            ProductType nonPerishable = new ProductType(); nonPerishable.setDescription("No Perecedero");

            productTypeRepository.saveAll(List.of(perishable, nonPerishable));

            Category bakery = new Category(); bakery.setDescription("Panificados y Pastelería");
            Category dairy = new Category(); dairy.setDescription("Lácteos");
            Category preparedFood = new Category(); preparedFood.setDescription("Comida Elaborada");
            Category othersCat = new Category(); othersCat.setDescription("Otros");

            categoryRepository.saveAll(List.of(bakery, dairy, preparedFood, othersCat));

            Product fineDoughs = new Product();
            fineDoughs.setName("Masas Finas / Macarons");
            fineDoughs.setRequiresRefrigeration(true);
            fineDoughs.setOriginalPackaging(false);
            fineDoughs.setProductType(perishable);
            fineDoughs.setCategory(bakery);

            Product bread = new Product();
            bread.setName("Pan de Masa Madre / Artesanal");
            bread.setRequiresRefrigeration(false);
            bread.setOriginalPackaging(false);
            bread.setProductType(perishable);
            bread.setCategory(bakery);

            Product milk = new Product();
            milk.setName("Leche Fresca");
            milk.setRequiresRefrigeration(true);
            milk.setOriginalPackaging(true);
            milk.setProductType(perishable);
            milk.setCategory(dairy);

            Product mainCourse = new Product();
            mainCourse.setName("Plato Principal (Aclarar en detalle)");
            mainCourse.setRequiresRefrigeration(true);
            mainCourse.setOriginalPackaging(false);
            mainCourse.setProductType(perishable);
            mainCourse.setCategory(preparedFood);

            Product wildcard = new Product();
            wildcard.setName("Otro / Especificar en detalle");
            wildcard.setRequiresRefrigeration(false);
            wildcard.setOriginalPackaging(false);
            wildcard.setProductType(perishable);
            wildcard.setCategory(othersCat);

            productRepository.saveAll(List.of(fineDoughs, bread, milk, mainCourse, wildcard));

            logger.info("All products have been created");
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
