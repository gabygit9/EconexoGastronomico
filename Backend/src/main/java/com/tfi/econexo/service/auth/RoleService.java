package com.tfi.econexo.service.auth;

import com.tfi.econexo.model.auth.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> findAll();
    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name);
    Role save(Role role);
    void deleteById(Long id);
    Role update(Role role);
}
