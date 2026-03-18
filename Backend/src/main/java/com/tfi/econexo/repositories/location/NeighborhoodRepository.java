package com.tfi.econexo.repositories.location;

import com.tfi.econexo.entities.location.Neighborhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long> {
}
