package com.tuconnect.dorm_connect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuconnect.dorm_connect.dto.User.UserDTO;
import com.tuconnect.dorm_connect.dto.auth.LoginRequest;
import com.tuconnect.dorm_connect.model.Roles;
import com.tuconnect.dorm_connect.model.User;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.security.JwtTokenProvider;
import com.tuconnect.dorm_connect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication)).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void register_ShouldReturnToken_WhenRegistrationIsSuccessful() throws Exception {
        UserDTO userDTO = new UserDTO(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                "password",
                null,
                User.Gender.MALE,
                "Computer Science",
                3,
                Roles.User
        );

        MockMultipartFile userDtoPart = new MockMultipartFile(
                "userDTO",
                "userDTO.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userDTO)
        );

        Authentication authentication = mock(Authentication.class);

        when(userService.createUser(any(UserDTO.class), any())).thenReturn(userDTO);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(
                        multipart("/auth/register")
                                .file(userDtoPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }
}