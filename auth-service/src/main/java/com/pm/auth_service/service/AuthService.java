package com.pm.auth_service.service;


import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService){
        this.userService=userService;
    }


    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){
        Optional<String> token= userService
                .findByEmail(loginRequestDTO.getEmail())
                .filter(u -> PasswordEncoder.matches(loginRequestDTO.getPassword(),
                u.getPassword()))
                .map(u->jwtUtil.generateToken(u.getEmail(),u.getRole()));

        return token;

    }

}
