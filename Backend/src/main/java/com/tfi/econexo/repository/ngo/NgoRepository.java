package com.tfi.econexo.repository.ngo;

import com.tfi.econexo.entity.ngo.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NgoRepository extends JpaRepository<Ngo, Long> {
}
