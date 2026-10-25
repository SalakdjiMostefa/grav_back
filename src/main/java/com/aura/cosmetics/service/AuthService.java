package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.AuthResponse;
import com.aura.cosmetics.dto.LoginRequest;
import com.aura.cosmetics.dto.RegisterRequest;
import com.aura.cosmetics.dto.WholesaleRegistrationRequest;
import com.aura.cosmetics.dto.WholesaleRequestDTO;
import com.aura.cosmetics.entity.User;
import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.enums.WholesaleStatus;
import com.aura.cosmetics.repository.UserRepository;
import com.aura.cosmetics.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        return buildResponse(saved);
    }

    public AuthResponse registerWholesale(WholesaleRegistrationRequest request) {
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            String cleanPhone = request.getPhone() != null ? request.getPhone().replaceAll("[^0-9]", "") : "";
            email = "pro." + (!cleanPhone.isEmpty() ? cleanPhone : System.currentTimeMillis()) + "@aura.pro";
        } else {
            email = email.trim();
        }

        // Si le client existe déjà (compte client connecté ou existant)
        java.util.Optional<User> existingUserOpt = userRepository.findByEmail(email);
        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            if (existing.getWholesaleStatus() == WholesaleStatus.APPROVED) {
                throw new RuntimeException("Ce compte est déjà un partenaire grossiste validé.");
            }
            if (existing.getWholesaleStatus() == WholesaleStatus.PENDING) {
                throw new RuntimeException("Votre demande grossiste est déjà en cours d'examen.");
            }

            // Mise à niveau du compte : conservation du même email, mot de passe et adresse
            existing.setWholesaleStatus(WholesaleStatus.PENDING);
            existing.setWholesaleRequestedAt(LocalDateTime.now());
            if (request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
                existing.setCompanyName(request.getCompanyName().trim());
            } else if (existing.getCompanyName() == null || existing.getCompanyName().isBlank()) {
                existing.setCompanyName("Établissement Pro");
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                existing.setBusinessPhone(request.getPhone().trim());
                if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                    existing.setPhone(request.getPhone().trim());
                }
            }
            if (request.getBusinessAddress() != null && !request.getBusinessAddress().isBlank()) {
                existing.setBusinessAddress(request.getBusinessAddress().trim());
                if (existing.getAddress() == null || existing.getAddress().isBlank()) {
                    existing.setAddress(request.getBusinessAddress().trim());
                }
            }
            User saved = userRepository.save(existing);
            return buildResponse(saved);
        }

        // Nouveau compte : mot de passe requis
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Le mot de passe doit comporter au moins 6 caractères.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getBusinessAddress());
        user.setRole(Role.ROLE_CLIENT);
        user.setWholesaleStatus(WholesaleStatus.PENDING);
        user.setCompanyName(request.getCompanyName() != null && !request.getCompanyName().isBlank() ? request.getCompanyName() : "Établissement Pro");
        user.setTradeRegister(request.getTradeRegister() != null ? request.getTradeRegister() : "");
        user.setBusinessPhone(request.getPhone());
        user.setBusinessAddress(request.getBusinessAddress());
        user.setWholesaleRequestedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return buildResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail().trim() : "";
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new RuntimeException("Email/Téléphone ou mot de passe incorrect."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect.");
        }

        return buildResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token manquant.");
        }

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Session expirée. Veuillez vous reconnecter.");
        }

        String email = jwtUtils.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Compte utilisateur introuvable."));

        return buildResponse(user);
    }

    @Transactional(readOnly = true)
    public List<WholesaleRequestDTO> getWholesaleRequests(WholesaleStatus statusFilter) {
        List<User> users;
        if (statusFilter != null) {
            users = userRepository.findByWholesaleStatus(statusFilter);
        } else {
            users = userRepository.findByWholesaleStatusNot(WholesaleStatus.NONE);
        }

        return users.stream().map(this::toWholesaleDTO).collect(Collectors.toList());
    }

    public WholesaleRequestDTO approveWholesale(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));

        user.setWholesaleStatus(WholesaleStatus.APPROVED);
        user.setRole(Role.ROLE_WHOLESALE);
        user.setWholesaleApprovedAt(LocalDateTime.now());
        User saved = userRepository.save(user);

        return toWholesaleDTO(saved);
    }

    public WholesaleRequestDTO rejectWholesale(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));

        user.setWholesaleStatus(WholesaleStatus.REJECTED);
        User saved = userRepository.save(user);

        return toWholesaleDTO(saved);
    }

    private WholesaleRequestDTO toWholesaleDTO(User user) {
        WholesaleRequestDTO dto = new WholesaleRequestDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone() != null ? user.getPhone() : user.getBusinessPhone());
        dto.setCompanyName(user.getCompanyName());
        dto.setTradeRegister(user.getTradeRegister());
        dto.setBusinessAddress(user.getBusinessAddress() != null ? user.getBusinessAddress() : user.getAddress());
        dto.setWholesaleStatus(user.getWholesaleStatus());
        dto.setRole(user.getRole());
        dto.setRequestedAt(user.getWholesaleRequestedAt());
        dto.setApprovedAt(user.getWholesaleApprovedAt());
        return dto;
    }

    public AuthResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvÃ© avec l'email : " + email));
        return buildResponse(user);
    }

    public AuthResponse toAuthResponse(User user) {
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        AuthResponse response = new AuthResponse(
                accessToken,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole(),
                user.getWholesaleStatus(),
                user.getCompanyName(),
                user.getTradeRegister()
        );
        response.setRefreshToken(refreshToken);
        return response;
    }
}
