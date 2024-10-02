package com.marek.onlinebookstore.service.user;

import com.marek.onlinebookstore.dto.user.login.UserLoginRequestDto;
import com.marek.onlinebookstore.dto.user.login.UserLoginResponseDto;
import com.marek.onlinebookstore.security.token.JwtUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.email(),
                        requestDto.password()
                )
        );

        User user = (User) authenticate.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        String token = jwtUtil.generateToken(user.getUsername(), claims);

        return new UserLoginResponseDto(token);
    }
}
