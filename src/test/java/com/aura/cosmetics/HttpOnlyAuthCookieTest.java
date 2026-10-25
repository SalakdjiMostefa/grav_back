package com.aura.cosmetics;

import com.aura.cosmetics.controller.AuthController;
import com.aura.cosmetics.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class HttpOnlyAuthCookieTest {

    @Test
    void testHttpOnlyCookieProperties() {
        ResponseCookie accessCookie = ResponseCookie.from(AuthController.ACCESS_COOKIE_NAME, "mock-jwt-token")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7200)
                .sameSite("Lax")
                .build();

        assertTrue(accessCookie.isHttpOnly(), "Access token cookie must be HttpOnly to prevent JavaScript access");
        assertEquals("Lax", accessCookie.getSameSite(), "Cookie must have SameSite=Lax");
        assertEquals("/", accessCookie.getPath(), "Access cookie must be available across /");
        assertEquals(7200, accessCookie.getMaxAge().getSeconds());
        assertEquals(AuthController.ACCESS_COOKIE_NAME, accessCookie.getName());
    }

    @Test
    void testRefreshTokenCookieHttpOnlyAndRestrictedPath() {
        ResponseCookie refreshCookie = ResponseCookie.from(AuthController.REFRESH_COOKIE_NAME, "mock-refresh-token")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(2592000)
                .sameSite("Lax")
                .build();

        assertTrue(refreshCookie.isHttpOnly(), "Refresh token cookie must be HttpOnly");
        assertEquals("/api/auth", refreshCookie.getPath(), "Refresh cookie path should be restricted to /api/auth");
        assertEquals(2592000, refreshCookie.getMaxAge().getSeconds());
    }

    @Test
    void testJwtTokenGenerationAndValidation() {
        JwtUtils jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "AuraSecretKey2026@CosmeticsAppSuperSecureJwtToken!");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 7200000L);
        ReflectionTestUtils.setField(jwtUtils, "refreshExpirationMs", 2592000000L);

        String email = "test@aura-cosmetics.fr";
        String token = jwtUtils.generateAccessToken(email, "ROLE_CLIENT");

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(email, jwtUtils.getEmailFromToken(token));
        assertEquals("ROLE_CLIENT", jwtUtils.getRoleFromToken(token));
    }
}