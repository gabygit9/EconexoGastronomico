package com.tfi.econexo.service.donation;

import com.tfi.econexo.dto.donation.catalog.CategoryDTO;
import com.tfi.econexo.dto.donation.catalog.ProductDTO;
import com.tfi.econexo.dto.donation.catalog.UnitOfMeasureDTO;

import java.util.List;

public interface CatalogService {
    List<CategoryDTO> getAllCategories();
    List<ProductDTO> getAllProducts();
    List<UnitOfMeasureDTO> getAllUnits();
}
