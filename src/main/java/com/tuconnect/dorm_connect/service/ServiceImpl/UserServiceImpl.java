package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.UserDTO;
import com.tuconnect.dorm_connect.mapper.UserMapper;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    public UserServiceImpl(UserRepository userRepository,UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Optional<UserDTO> getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(userMapper::toDTO);
    }

    public Long getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    public UserDTO createUser(UserDTO dto) {
        User user = new User();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setProfileImageUrl(dto.profileImageUrl());
        user.setMajor(dto.major());
        user.setYear(dto.year());
        user.setRole(Roles.Users);

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    public UserDTO updateUser(Long userId, UserDTO updatedUserDTO) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        existingUser.setFirstName(updatedUserDTO.firstName());
        existingUser.setLastName(updatedUserDTO.lastName());
        existingUser.setEmail(updatedUserDTO.email());
        existingUser.setPassword(updatedUserDTO.password());
        existingUser.setProfileImageUrl(updatedUserDTO.profileImageUrl());
        existingUser.setMajor(updatedUserDTO.major());
        existingUser.setYear(updatedUserDTO.year());


        User savedUser = userRepository.save(existingUser);
        return userMapper.toDTO(savedUser);
    }

    public void deleteUser(Long userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


        userRepository.delete(existingUser);
    }
}
