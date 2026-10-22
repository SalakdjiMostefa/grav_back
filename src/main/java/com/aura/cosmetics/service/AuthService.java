package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.AuthResponse;
import com.aura.cosmetics.dto.LoginRequest;
import com.aura.cosmetics.dto.RegisterRequest;
import com.aura.cosmetics.entity.User;
import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.repository.UserRepository;
import com.aura.cosmetics.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /** Inscription d'un nouveau CLIENT */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cet email : " + request.getEmail());
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                request.getAddress(),
                Role.ROLE_CLIENT
        );

        User saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getEmail(), saved.getRole().name());

        return buildResponse(token, saved);
    }

    /** Connexion pour ADMIN et CLIENTS */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect.");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return buildResponse(token, user);
    }

    private AuthResponse buildResponse(String token, User user) {
        return new AuthResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole()
        );
    }
}
