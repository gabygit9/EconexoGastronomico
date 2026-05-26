package com.tfi.econexo.mappers;

import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.entity.security.UserSec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "encryptedPassword")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialNonExpired", constant = "true")
    @Mapping(target = "rolesList", expression = "java(java.util.Set.of(role))")
    UserSec toEntity(String email, String encryptedPassword, Role role);
}
