package com.tfi.econexo.repository.logistics;

import com.tfi.econexo.entity.logistics.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
