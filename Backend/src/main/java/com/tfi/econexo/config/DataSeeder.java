package com.tfi.econexo.config;

import com.tfi.econexo.entities.location.City;
import com.tfi.econexo.entities.location.Neighborhood;
import com.tfi.econexo.repositories.location.CityRepository;
import com.tfi.econexo.repositories.location.NeighborhoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final CityRepository cityRepository;
    private final NeighborhoodRepository neighborhoodRepository;

    @Override
    public void run(String... args) throws Exception {
        if(cityRepository.count() == 0){
            City cordoba = new City();
            cordoba.setName("Cordoba");
            cordoba = cityRepository.save(cordoba);

            if(neighborhoodRepository.count() == 0){
                Neighborhood n1 = new Neighborhood();
                n1.setName("General Paz");
                n1.setCity(cordoba);

                neighborhoodRepository.save(n1);
            }
        }
    }
}
