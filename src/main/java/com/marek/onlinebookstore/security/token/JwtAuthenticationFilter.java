package com.marek.onlinebookstore.security.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenFromRequest = getTokenFromRequest(request);
        tokenValidation(tokenFromRequest);

        filterChain.doFilter(request, response);
    }

    private void tokenValidation(String tokenFromRequest) {
        if (tokenFromRequest != null && jwtUtil.isValid(tokenFromRequest)) {
            String username = jwtUtil.getUserName(tokenFromRequest);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authenticationToken
                    = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authenticationToken);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return extractToken(authorization);
    }

    private static String extractToken(String authorization) {
        if (StringUtils.hasText(authorization)
                && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}

