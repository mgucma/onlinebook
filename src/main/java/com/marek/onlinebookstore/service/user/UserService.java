package com.marek.onlinebookstore.service.user;

import com.marek.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.user.UserResponseDto;
import com.marek.onlinebookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}

