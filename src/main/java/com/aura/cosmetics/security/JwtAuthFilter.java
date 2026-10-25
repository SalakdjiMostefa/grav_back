package com.aura.cosmetics.security;

import com.aura.cosmetics.entity.User;
import com.aura.cosmetics.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ACCESS_COOKIE_NAME = "aura_access_token";

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtUtils.validateToken(token)) {
            String email = jwtUtils.getEmailFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userOpt.get(),
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // 1. PrioritÃ© au header Authorization (Bearer <token>)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty() && !"null".equalsIgnoreCase(token) && !"undefined".equalsIgnoreCase(token)) {
                return token;
            }
        }

        // 2. Cookie HttpOnly "aura_access_token"
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_COOKIE_NAME.equals(cookie.getName())) {
                    String val = cookie.getValue();
                    if (val != null && !val.isBlank() && !"null".equalsIgnoreCase(val)) {
                        return val;
                    }
                }
            }
        }

        return null;
    }
}