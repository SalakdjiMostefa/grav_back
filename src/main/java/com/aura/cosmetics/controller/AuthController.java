package com.aura.cosmetics.controller;

import com.aura.cosmetics.dto.AuthResponse;
import com.aura.cosmetics.dto.LoginRequest;
import com.aura.cosmetics.dto.RegisterRequest;
import com.aura.cosmetics.dto.WholesaleRegistrationRequest;
import com.aura.cosmetics.entity.User;
import com.aura.cosmetics.security.JwtUtils;
import com.aura.cosmetics.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String ACCESS_COOKIE_NAME = "aura_access_token";
    public static final String REFRESH_COOKIE_NAME = "aura_refresh_token";

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .headers(buildAuthCookies(response.getToken(), response.getRefreshToken()))
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register-wholesale")
    public ResponseEntity<?> registerWholesale(@RequestBody WholesaleRegistrationRequest request) {
        try {
            AuthResponse response = authService.registerWholesale(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .headers(buildAuthCookies(response.getToken(), response.getRefreshToken()))
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok()
                    .headers(buildAuthCookies(response.getToken(), response.getRefreshToken()))
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody(required = false) Map<String, String> requestBody,
            HttpServletRequest request) {
        try {
            String refreshToken = null;
            if (requestBody != null && requestBody.containsKey("refreshToken")) {
                refreshToken = requestBody.get("refreshToken");
            }
            if (refreshToken == null || refreshToken.isBlank()) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie c : cookies) {
                        if (REFRESH_COOKIE_NAME.equals(c.getName())) {
                            refreshToken = c.getValue();
                            break;
                        }
                    }
                }
            }

            if (refreshToken == null || refreshToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Aucun refresh token fourni."));
            }

            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok()
                    .headers(buildAuthCookies(response.getToken(), response.getRefreshToken()))
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCleanCookie(ACCESS_COOKIE_NAME, "/").toString());
        headers.add(HttpHeaders.SET_COOKIE, createCleanCookie(REFRESH_COOKIE_NAME, "/api/auth").toString());

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().headers(headers).body(Map.of("message", "DÃ©connexion rÃ©ussie"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(authService.toAuthResponse(user));
        }

        // Si le filtre n'a pas positionnÃ© le user mais que le cookie existe
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (ACCESS_COOKIE_NAME.equals(c.getName())) {
                    String token = c.getValue();
                    if (token != null && jwtUtils.validateToken(token)) {
                        String email = jwtUtils.getEmailFromToken(token);
                        try {
                            return ResponseEntity.ok(authService.getCurrentUser(email));
                        } catch (Exception ignored) {}
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Non connectÃ© ou session expirÃ©e."));
    }

    private HttpHeaders buildAuthCookies(String accessToken, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        if (accessToken != null && !accessToken.isBlank()) {
            ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE_NAME, accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7200) // 2 heures
                    .sameSite("Lax")
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/api/auth")
                    .maxAge(2592000) // 30 jours
                    .sameSite("Lax")
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        return headers;
    }

    private ResponseCookie createCleanCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .path(path)
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}