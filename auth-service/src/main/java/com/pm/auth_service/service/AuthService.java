package com.pm.auth_service.service;


import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService){
        this.userService=userService;
    }


    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){
        Optional<User> user= userService.findByEmail(LoginRequestDTO.getEmail());

    }

}
