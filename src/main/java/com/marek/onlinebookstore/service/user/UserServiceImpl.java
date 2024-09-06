package com.marek.onlinebookstore.service.user;

import com.marek.onlinebookstore.dto.user.register.UserRegistrationRequestDto;
import com.marek.onlinebookstore.dto.user.register.UserRegistrationResponseDto;
import com.marek.onlinebookstore.exception.RegistrationException;
import com.marek.onlinebookstore.mapper.UserMapper;
import com.marek.onlinebookstore.model.Role;
import com.marek.onlinebookstore.model.RoleName;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.repository.user.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final RoleName USER = RoleName.ROLE_USER;
    private static final Role role = new Role();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserRegistrationResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        checkIfUserExists(request.email());
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        role.setName(USER);
        user.setRoles(Set.of(role));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private void checkIfUserExists(String email) throws RegistrationException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationException("User with email " + email + " already exists");
        }
    }
}
