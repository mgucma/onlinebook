package com.marek.onlinebookstore.controller;

import com.marek.onlinebookstore.dto.user.login.UserLoginRequestDto;
import com.marek.onlinebookstore.dto.user.login.UserLoginResponseDto;
import com.marek.onlinebookstore.dto.user.register.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.user.register.UserRegistrationResponseDto;
import com.marek.onlinebookstore.exception.RegistrationException;
import com.marek.onlinebookstore.service.user.AuthenticationService;
import com.marek.onlinebookstore.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request) throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(
            @RequestBody UserLoginRequestDto loginRequestDto) {
        return authenticationService.authenticate(loginRequestDto);
    }
}

