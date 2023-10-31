package com.ute.studentconsulting.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ute.studentconsulting.entity.RefreshToken;
import com.ute.studentconsulting.exception.AppException;
import com.ute.studentconsulting.model.TokenModel;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenUtility {
    @Value("${student-consulting.app.secret-key}")
    private String secretKey;

    @Value("${student-consulting.app.access-token-expires-ms}")
    private Long accessTokenExpiresMs;

    @Value("${student-consulting.app.refresh-token-expires-ms}")
    private Long refreshTokenExpiresMs;

    @Value("${student-consulting.app.cookie-name}")
    private String cookieName;

    private final ObjectMapper objectMapper;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + accessTokenExpiresMs))
                .signWith(key(), Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token không được hỗ trợ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string trống: {}", e.getMessage());
        }
        return false;
    }

    public String getRefreshTokenByValue(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, cookieName);
        return (cookie != null) ? cookie.getValue() : null;
    }


    public ResponseCookie setCookie(String token) {
        return ResponseCookie.from(cookieName, token)
                .secure(true)
                .httpOnly(true)
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("None")
                .build();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public RefreshToken generateRefreshToken() {
        return new RefreshToken(
                UUID.randomUUID().toString(),
                new Date(new Date().getTime() + refreshTokenExpiresMs),
                true);
    }

    public RefreshToken generateRefreshToken(String parent) {
        try {
            var secureRandom = new SecureRandom();
            var tokenObj = new TokenModel(String.valueOf(secureRandom.nextLong()), parent);
            var json = objectMapper.writeValueAsString(tokenObj);
            var bytes = json.getBytes(StandardCharsets.UTF_8);
            var token = Base64.getUrlEncoder().encodeToString(bytes);
            return new RefreshToken(
                    token,
                    new Date(new Date().getTime() + refreshTokenExpiresMs),
                    true);
        } catch (JsonProcessingException e) {
            log.error("Lỗi mã hóa token object thành JSON: {}", e.getMessage());
            throw new AppException("Lỗi mã hóa token object thành JSON: " + e.getMessage());
        }

    }

    public ResponseCookie clearCookie() {
        return ResponseCookie
                .from(cookieName, "")
                .secure(true)
                .httpOnly(true)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("None")
                .build();
    }
}
