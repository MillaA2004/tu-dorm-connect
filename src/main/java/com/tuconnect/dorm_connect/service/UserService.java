package com.tuconnect.dorm_connect.service;

import com.tuconnect.dorm_connect.dto.User.UserDTO;

import java.util.Optional;

public interface UserService {

    Optional<UserDTO> getUserById(Long id);

    Long getUserIdFromEmail(String email);

    UserDTO createUser(UserDTO dto);

    UserDTO updateUser(Long id, UserDTO updatedUserDTO);

    void deleteUser(Long id);
}
