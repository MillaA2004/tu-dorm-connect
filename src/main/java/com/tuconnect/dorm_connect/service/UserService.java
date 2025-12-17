package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.User.UserDTO;
import com.tuconnect.dorm_connect.model.Roles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {

    Optional<UserDTO> getUserById(Long id);

    Long getUserIdFromEmail(String email);

    void softDeleteUser(Long id);

    UserDTO updateUser(Long userId, UserDTO updatedUserDTO, MultipartFile file) throws IOException;

    UserDTO createUser(UserDTO dto, MultipartFile file) throws IOException;

    void suspendUser(Long userId, long minutes);

    void unsuspendUser(Long userId);

    void setRole(Long userId, Roles role);
}
