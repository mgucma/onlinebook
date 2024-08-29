package com.marek.onlinebookstore.service.user;

import com.marek.onlinebookstore.dto.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.UserResponseDto;
import com.marek.onlinebookstore.exception.RegistrationException;
import com.marek.onlinebookstore.mapper.UserMapper;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request) {
        checkIfUserExists(request.getEmail());
        User user = userMapper.toModel(request);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private void checkIfUserExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationException("User already exists");
        }
    }
}
