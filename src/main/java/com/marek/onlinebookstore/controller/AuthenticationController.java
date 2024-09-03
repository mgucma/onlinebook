package com.marek.onlinebookstore.controller;

import com.marek.onlinebookstore.dto.user.UserLoginRequestDto;
import com.marek.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.user.UserResponseDto;
import com.marek.onlinebookstore.exception.RegistrationException;
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

    @PostMapping("/registration")
    public UserResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request) throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody @Valid UserLoginRequestDto loginRequestDto) {
        return true;
    }
}

