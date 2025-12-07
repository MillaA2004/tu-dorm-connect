package com.tuconnect.dorm_connect.service.ServiceImpl;

import com.tuconnect.dorm_connect.dto.User.UserDTO;
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

    public Optional<UserDTO> getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
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
        user.setGender(dto.gender());
        user.setMajor(dto.major());
        user.setYear(dto.year());
        user.setRole(dto.role());

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserDTO updatedUserDTO) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        existingUser.setFirstName(updatedUserDTO.firstName());
        existingUser.setLastName(updatedUserDTO.lastName());
        existingUser.setEmail(updatedUserDTO.email());
        existingUser.setPassword(updatedUserDTO.password());
        existingUser.setProfileImageUrl(updatedUserDTO.profileImageUrl());
        existingUser.setGender(updatedUserDTO.gender());
        existingUser.setMajor(updatedUserDTO.major());
        existingUser.setYear(updatedUserDTO.year());
        existingUser.setRole(updatedUserDTO.role());


        User savedUser = userRepository.save(existingUser);
        return userMapper.toDTO(savedUser);
    }

    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));


        userRepository.delete(existingUser);
    }
}
