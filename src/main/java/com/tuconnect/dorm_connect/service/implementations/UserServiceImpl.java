package com.tuconnect.dorm_connect.service.implementations;

import com.tuconnect.dorm_connect.dto.User.UserDTO;
import com.tuconnect.dorm_connect.mapper.UserMapper;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.service.CloudinaryService;
import com.tuconnect.dorm_connect.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TableGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    public Optional<UserDTO> getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findByIdAndDeletedFalse(userId);
        return userOptional.map(userMapper::toDTO);
    }

    public Long getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found for major: " + email));
    }



    @Transactional
    public UserDTO createUser(UserDTO dto, MultipartFile file) throws IOException {

        if(userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("user with such email already exists");
        }

        String profileImage = null;

        if(file!=null &&!file.isEmpty()) {
            profileImage = cloudinaryService.uploadFile(file);
        }

        User user = new User();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setProfileImageUrl(profileImage);
        user.setMajor(dto.major());
        user.setGender(dto.gender());
        user.setAcademicYear(dto.year());
        user.setGender(dto.gender());
        user.setRole(Roles.User);

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserDTO updatedUserDTO, MultipartFile file) throws IOException {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        String profileImage = existingUser.getProfileImageUrl();

        if(file!=null &&!file.isEmpty()) {
            profileImage = cloudinaryService.uploadFile(file);
        }

        existingUser.setFirstName(updatedUserDTO.firstName());
        existingUser.setLastName(updatedUserDTO.lastName());
        existingUser.setEmail(updatedUserDTO.email());
        existingUser.setPassword(updatedUserDTO.password());
        existingUser.setProfileImageUrl(profileImage);
        existingUser.setGender(updatedUserDTO.gender());
        existingUser.setMajor(updatedUserDTO.major());
        existingUser.setGender(updatedUserDTO.gender());
        existingUser.setAcademicYear(updatedUserDTO.year());


        User savedUser = userRepository.save(existingUser);
        return userMapper.toDTO(savedUser);
    }


    @Transactional
    public void softDeleteUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (u.isDeleted()) return;

        u.setDeleted(true);
        u.setDeletedAt(Instant.now());


        u.setFirstName("Deleted");
        u.setLastName("User");
        u.setProfileImageUrl(null);
        u.setMajor("");
        u.setAcademicYear(0);



        userRepository.save(u);
    }
}
