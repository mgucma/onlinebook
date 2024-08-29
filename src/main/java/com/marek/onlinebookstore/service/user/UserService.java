package com.marek.onlinebookstore.service.user;

import com.marek.onlinebookstore.dto.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);
}
