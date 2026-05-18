package com.tfi.econexo.repository.logistics;

import com.tfi.econexo.entity.logistics.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
}
