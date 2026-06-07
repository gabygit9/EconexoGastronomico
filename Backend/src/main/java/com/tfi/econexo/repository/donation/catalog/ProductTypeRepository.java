package com.tfi.econexo.repository.donation.catalog;

import com.tfi.econexo.model.donation.catalog.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
}
