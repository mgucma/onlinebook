package com.marek.onlinebookstore.mapper;

import com.marek.onlinebookstore.config.MapperConfig;
import com.marek.onlinebookstore.dto.user.register.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.user.register.UserRegistrationResponseDto;
import com.marek.onlinebookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto registrationRequestDto);
}
