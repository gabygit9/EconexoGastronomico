package com.tfi.econexo.repositories.logistics;

import com.tfi.econexo.entities.logistics.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
}
