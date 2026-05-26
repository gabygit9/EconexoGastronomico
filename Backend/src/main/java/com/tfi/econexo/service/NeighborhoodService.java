package com.tfi.econexo.service;

import com.tfi.econexo.entity.location.Neighborhood;

import java.util.List;
import java.util.Optional;

public interface NeighborhoodService {

    List<Neighborhood> findAll();
    Optional<Neighborhood> findById(Long id);
}
