package com.tuconnect.dorm_connect.controller;

import com.tuconnect.dorm_connect.dto.User.UserDTO;
import com.tuconnect.dorm_connect.dto.auth.JwtResponse;
import com.tuconnect.dorm_connect.dto.auth.LoginRequest;
import com.tuconnect.dorm_connect.security.JwtAuthenticationFilter;
import com.tuconnect.dorm_connect.repository.UserRepository;
import com.tuconnect.dorm_connect.security.JwtTokenProvider;
import com.tuconnect.dorm_connect.security.UserPrincipal;
import com.tuconnect.dorm_connect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String email = authentication.getName();
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User missing"));

            if (user.isDeleted()) {
                throw new DisabledException("Account deleted");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal && userPrincipal.isSuspendedNow()) {
                Map<String, Object> body = buildBody(userPrincipal);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (LockedException ex) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", "SUSPENDED");
            body.put("message", "You've been suspended.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
    }

    public static Map<String, Object> buildBody(UserPrincipal userPrincipal) {
        return JwtAuthenticationFilter.buildBody(userPrincipal);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JwtResponse> register(
            @RequestPart UserDTO userDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        userService.createUser(userDTO, file);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.email(),
                        userDTO.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(token));
    }
}