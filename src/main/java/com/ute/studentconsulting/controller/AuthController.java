package com.ute.studentconsulting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ute.studentconsulting.entity.RoleName;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.model.AuthModel;
import com.ute.studentconsulting.model.TokenModel;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.payloads.request.LoginRequest;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.security.service.impl.UserDetailsImpl;
import com.ute.studentconsulting.security.token.TokenUtility;
import com.ute.studentconsulting.service.RefreshTokenService;
import com.ute.studentconsulting.service.RoleService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.utility.UserUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final RoleService roleService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenUtility tokenUtility;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final UserUtility userUtility;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserPayload request) {
        try {
            return handleRegister(request);
        } catch (Exception e) {
            log.error("Lỗi đăng ký tài khoản: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi đăng ký tài khoản")
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return handleLogin(request);
        } catch (DisabledException e) {
            log.error("Tài khoản đã bị khóa: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Tài khoản đã bị khóa"),
                    HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Lỗi đăng nhập: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            return handleRefreshToken(request);
        } catch (Exception e) {
            log.error("Lỗi làm mới token: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi làm mới token"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            return handleLogout(request);
        } catch (Exception e) {
            log.error("Lỗi đăng xuất: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi đăng xuất"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleLogout(HttpServletRequest request) {
        var tokenValue = tokenUtility.getRefreshTokenByValue(request);
        if (StringUtils.hasText(tokenValue)) {
            var tokenAuth = refreshTokenService.findById(tokenValue);
            var parent = tokenAuth.getParent() != null ? tokenAuth.getParent() : tokenAuth;
            refreshTokenService.deleteById(parent.getToken());
        }
        var response = tokenUtility.clearCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.toString())
                .body(new MessageResponse(true, "Đăng xuất thành công"));
    }

    private ResponseEntity<?> handleRefreshToken(HttpServletRequest request) {
        var tokenValue = tokenUtility.getRefreshTokenByValue(request);
        if (!StringUtils.hasText(tokenValue)) {
            return unauthorizedResponse();
        }

        var tokenAuth = refreshTokenService.findById(tokenValue);

        if (tokenAuth != null) {
            var parent = tokenAuth.getParent() != null ? tokenAuth.getParent() : tokenAuth;

            if (tokenAuth.getStatus() && tokenAuth.getExpires().compareTo(new Date()) > 0) {
                refreshTokenService.deleteByParent(parent);
                var nextToken = tokenUtility.generateRefreshToken(parent.getToken());
                nextToken.setUser(tokenAuth.getUser());
                nextToken.setParent(parent);

                var savedToken = refreshTokenService.save(nextToken);
                var accessToken = tokenUtility.generateToken(nextToken.getUser().getPhone());

                var cookie = tokenUtility.setCookie(savedToken.getToken());
                var response = new AuthModel(
                        accessToken,
                        nextToken.getUser().getName(),
                        nextToken.getUser().getRole().getName().name(),
                        nextToken.getUser().getAvatar());

                if (tokenAuth.getParent() == null) {
                    tokenAuth.setStatus(false);
                    refreshTokenService.save(tokenAuth);
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new ApiResponse<>(true, response));
            }
            refreshTokenService.deleteById(parent.getToken());
        } else {
            try {
                var bytes = Base64.getUrlDecoder().decode(tokenValue);
                var jsonValue = new String(bytes, StandardCharsets.UTF_8);
                var tokenObj = objectMapper.readValue(jsonValue, TokenModel.class);
                refreshTokenService.deleteById(tokenObj.getP());
            } catch (JsonProcessingException e) {
                log.error("Lỗi dữ liệu JSON không hợp lệ trong token: {}", e.getMessage());
            }
        }
        return unauthorizedResponse();
    }

    private ResponseEntity<?> unauthorizedResponse() {
        var cookie = tokenUtility.clearCookie();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return new ResponseEntity<>(new MessageResponse(false, "Không đủ quyền truy cập"), headers, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> handleLogin(LoginRequest request) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var authority = userDetails.getAuthorities().stream().findFirst();

        if (authority.isEmpty()) {
            return new ResponseEntity<>(
                    new MessageResponse(false, "Không đủ quyền truy cập"),
                    HttpStatus.UNAUTHORIZED);
        }

        var token = tokenUtility.generateToken(userDetails.getUsername());
        var refreshToken = tokenUtility.generateRefreshToken();
        var user = userService.findById(userDetails.getId());
        refreshToken.setUser(user);
        var savedToken = refreshTokenService.save(refreshToken);
        var cookie = tokenUtility.setCookie(savedToken.getToken());
        var response = new AuthModel(
                token,
                user.getName(),
                authority.get().getAuthority(),
                user.getAvatar());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(true, response));
    }

    private ResponseEntity<?> handleRegister(UserPayload request) {
        var error = userUtility.validationNewUser(request, false);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }
        var role = roleService.findByName(RoleName.ROLE_USER);
        var user = new User(
                request.getName(),
                request.getEmail().toLowerCase(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                true,
                role);
        user.setOccupation(request.getOccupation());
        userService.save(user);
        return new ResponseEntity<>(
                new MessageResponse(true, "Tạo tài khoản thành công"),
                HttpStatus.CREATED);
    }


}
