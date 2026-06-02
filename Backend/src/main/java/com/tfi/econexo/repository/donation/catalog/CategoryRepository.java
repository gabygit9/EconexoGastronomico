package com.tfi.econexo.repository.donation.catalog;

import com.tfi.econexo.model.donation.catalog.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
