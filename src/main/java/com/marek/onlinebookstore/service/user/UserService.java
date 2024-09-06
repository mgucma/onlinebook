package com.marek.onlinebookstore.service.user;

import com.marek.onlinebookstore.dto.user.register.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.user.register.UserRegistrationResponseDto;
import com.marek.onlinebookstore.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;
}

