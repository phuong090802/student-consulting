package com.ute.studentconsulting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ute.studentconsulting.entity.RoleName;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ForbiddenException;
import com.ute.studentconsulting.exception.ServerException;
import com.ute.studentconsulting.exception.UnauthorizedException;
import com.ute.studentconsulting.model.AuthModel;
import com.ute.studentconsulting.model.TokenModel;
import com.ute.studentconsulting.model.CurrentUserModel;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.payloads.request.LoginRequest;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.ErrorResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.security.service.impl.UserDetailsImpl;
import com.ute.studentconsulting.security.token.TokenUtility;
import com.ute.studentconsulting.service.RefreshTokenService;
import com.ute.studentconsulting.service.RoleService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.UserUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    private final AuthUtility authUtility;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            return handleGetCurrentUser();
        } catch (Exception e) {
            log.error("Lỗi lấy thông tin người dùng hiện tại: {}", e.getMessage());
            throw new ServerException("Lỗi lấy thông tin người dùng hiện tại", e.getMessage(), 10014);
        }
    }

    private ResponseEntity<?> handleGetCurrentUser() {
        var user = authUtility.getCurrentUser();
        var response = new CurrentUserModel(
                user.getName(),
                user.getRole().getName().name(),
                user.getAvatar());
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, response));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserPayload request) {
        try {
            return handleRegister(request);
        } catch (Exception e) {
            log.error("Lỗi đăng ký tài khoản: {}", e.getMessage());
            throw new ServerException("Lỗi đăng ký tài khoản", e.getMessage(), 10015);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return handleLogin(request);
        } catch (BadCredentialsException e) {
            log.error("Tài khoản đã bị khóa: {}", e.getMessage());
            throw new BadRequestException(
                    "Vui lòng kiểm lại tên đăng nhập và mật khẩu",
                    e.getMessage(), 10016);
        } catch (DisabledException e) {
            log.error("Tài khoản đã bị khóa: {}", e.getMessage());
            throw new ForbiddenException("Tài khoản đã bị khóa", e.getMessage(), 10017);
        } catch (Exception e) {
            log.error("Lỗi đăng nhập: {}", e.getMessage());
            throw new ServerException("Lỗi đăng nhập", e.getMessage(), 10018);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            return handleRefreshToken(request);
        } catch (Exception e) {
            log.error("Lỗi làm mới token: {}", e.getMessage());
            throw new ServerException("Lỗi làm mới token", e.getMessage(), 10019);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            return handleLogout(request);
        } catch (Exception e) {
            log.error("Lỗi đăng xuất: {}", e.getMessage());
            throw new ServerException("Lỗi đăng xuất", e.getMessage(), 10020);
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
            return badRequest();
        }

        var tokenAuth = refreshTokenService.findById(tokenValue);

        if (tokenAuth != null) {
            var parent = tokenAuth.getParent() != null ? tokenAuth.getParent() : tokenAuth;

            if (tokenAuth.getStatus() && tokenAuth.getExpires().compareTo(new Date()) > 0) {
                if (tokenAuth.getParent() == null) {
                    tokenAuth.setStatus(false);
                    refreshTokenService.save(tokenAuth);
                }
                refreshTokenService.deleteByParent(parent);
                var nextToken = tokenUtility.generateRefreshToken(parent.getToken());
                nextToken.setUser(parent.getUser());
                nextToken.setParent(parent);

                var savedToken = refreshTokenService.save(nextToken);
                var accessToken = tokenUtility.generateToken(nextToken.getUser().getPhone());

                var cookie = tokenUtility.setCookie(savedToken.getToken());
                var response = new AuthModel(
                        accessToken,
                        nextToken.getUser().getName(),
                        nextToken.getUser().getRole().getName().name(),
                        nextToken.getUser().getAvatar());

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
                log.error("Lỗi dữ liệu JSON không hợp lệ trong token, (token cha hoặc token không hợp lệ): {}", e.getMessage());
            }
        }
        return badRequest();
    }

    private ResponseEntity<?> badRequest() {
        var cookie = tokenUtility.clearCookie();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return new ResponseEntity<>(new ErrorResponse(false, "Refresh token không thành công", "Lỗi xảy ra khi refresh token", 10032), headers, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> handleLogin(LoginRequest request) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var authority = userDetails.getAuthorities().stream().findFirst();
        if (authority.isEmpty()) {
            throw new UnauthorizedException("Không đủ quyền truy cập", "Quyền truy cập của người dùng trống", 10021);
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
        userUtility.validationNewUser(request, false);
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
