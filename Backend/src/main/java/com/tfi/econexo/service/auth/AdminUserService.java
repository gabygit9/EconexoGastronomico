package com.tfi.econexo.service.auth;

import com.tfi.econexo.dto.auth.admin.UserAdminResponseDTO;
import com.tfi.econexo.model.enums.RegistrationStatus;

import java.util.List;

public interface AdminUserService {

    List<UserAdminResponseDTO> getAllRegisteredUsers();

    void updateUserStatus(Long userId, RegistrationStatus status);
}
