package com.tfi.econexo.service.auth;


import com.tfi.econexo.entity.security.UserSec;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserSec> findAll();
    Optional<UserSec> findById(Long id);
    UserSec save(UserSec user);
    void deleteById(Long id);
    UserSec update(UserSec user);
    String encryptPassword(String password);
    Optional<UserSec> findByEmail(String email);
}
