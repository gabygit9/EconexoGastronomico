package com.tfi.econexo.repository.auth;

import com.tfi.econexo.model.auth.UserSec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserSec, Long> {

    Optional<UserSec> findUserEntityByEmail(String email);
}
