package com.pm.auth_service.service;


import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.model.User;
import com.pm.auth_service.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService,PasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
    }


    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){
        Optional<String> token= userService
                .findByEmail(loginRequestDTO.getEmail())
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(),
                u.getPassword()))
                .map(u->jwtUtil.generateToken(u.getEmail(),u.getRole()));

        System.out.println("User found: " + userService.findByEmail(loginRequestDTO.getEmail()));


        return token;

    }

    public boolean validateToken(String token){
        try{
            jwtUtil.validateToken(token);
            return true;
        }
        catch (JwtException e){
            return false;
        }
    }

}
