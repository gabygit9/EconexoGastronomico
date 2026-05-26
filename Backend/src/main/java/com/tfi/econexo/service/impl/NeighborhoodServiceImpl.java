package com.tfi.econexo.service.impl;

import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.repository.location.NeighborhoodRepository;
import com.tfi.econexo.service.NeighborhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NeighborhoodServiceImpl implements NeighborhoodService {

    private final NeighborhoodRepository neighborhoodRepository;

    @Override
    public List<Neighborhood> findAll() {
        return neighborhoodRepository.findAll();
    }

    @Override
    public Optional<Neighborhood> findById(Long id) {
        return neighborhoodRepository.findById(id);
    }
}
