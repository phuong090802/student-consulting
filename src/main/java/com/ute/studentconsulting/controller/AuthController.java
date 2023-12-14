package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.RoleName;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.UnauthorizedException;
import com.ute.studentconsulting.model.AuthModel;
import com.ute.studentconsulting.model.CurrentUserModel;
import com.ute.studentconsulting.payload.UserPayload;
import com.ute.studentconsulting.payload.request.LoginRequest;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.ErrorResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.security.service.impl.UserDetailsImpl;
import com.ute.studentconsulting.security.token.TokenUtils;
import com.ute.studentconsulting.service.RefreshTokenService;
import com.ute.studentconsulting.service.RoleService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.util.AuthUtils;
import com.ute.studentconsulting.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final RoleService roleService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;
    private final AuthUtils authUtils;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return handleGetCurrentUser();
    }

    private ResponseEntity<?> handleGetCurrentUser() {
        var user = authUtils.getCurrentUser();
        var occupation = user.getOccupation() != null ? user.getOccupation() : "";
        var response = new CurrentUserModel(user.getName(), user.getEmail(), user.getPhone(),
                user.getRole().getName().name(), occupation, user.getAvatar());
        return ResponseEntity.ok().body(new ApiSuccessResponse<>(response));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserPayload request) {
        return handleRegister(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return handleLogin(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return handleRefreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return handleLogout(request);
    }

    private ResponseEntity<?> handleLogout(HttpServletRequest request) {
        var tokenValue = tokenUtils.getRefreshTokenByValue(request);
        if (StringUtils.hasText(tokenValue)) {
            var tokenAuth = refreshTokenService.findById(tokenValue);
            var parent = tokenAuth.getParent() != null ? tokenAuth.getParent() : tokenAuth;
            refreshTokenService.deleteByParent(parent);
            refreshTokenService.deleteById(parent.getToken());
        }
        var response = tokenUtils.clearCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.toString())
                .body(new SuccessResponse(true, "Đăng xuất thành công"));
    }

    private ResponseEntity<?> handleRefreshToken(HttpServletRequest request) {
        var tokenValue = tokenUtils.getRefreshTokenByValue(request);
        if (!StringUtils.hasText(tokenValue)) {
            return forbidden();
        }

        var tokenAuth = refreshTokenService.findById(tokenValue);
        if (tokenAuth == null) {
            return forbidden();
        }
        var parent = tokenAuth.getParent() != null ? tokenAuth.getParent() : tokenAuth;
        if (tokenAuth.getStatus() && tokenAuth.getExpires().compareTo(new Date()) > 0) {

            var user = parent.getUser();
            var nextToken = tokenUtils.generateRefreshToken();
            nextToken.setUser(user);
            nextToken.setParent(parent);

            var savedToken = refreshTokenService.save(nextToken);
            var accessToken = tokenUtils.generateToken(user.getPhone());

            tokenAuth.setStatus(false);
            refreshTokenService.save(tokenAuth);
            var occupation = user.getOccupation() != null ? user.getOccupation() : "";

            var response = new AuthModel(accessToken, user.getName(), user.getEmail(), user.getPhone(),
                    user.getRole().getName().name(), occupation, user.getAvatar());

            var cookie = tokenUtils.setCookie(savedToken.getToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new ApiSuccessResponse<>(response));
        }
        refreshTokenService.deleteByParent(parent);
        refreshTokenService.deleteById(parent.getToken());
        return forbidden();

    }

    private ResponseEntity<?> forbidden() {
        var cookie = tokenUtils.clearCookie();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ErrorResponse(false,
                        "Refresh token không thành công", "Lỗi xảy ra khi refresh token", 10016));
    }

    private ResponseEntity<?> handleLogin(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var authority = userDetails.getAuthorities().stream().findFirst();
        if (authority.isEmpty()) {
            throw new UnauthorizedException("Không đủ quyền truy cập", "Quyền truy cập của người dùng trống", 10017);
        }
        var token = tokenUtils.generateToken(userDetails.getUsername());
        var refreshToken = tokenUtils.generateRefreshToken();
        var user = userService.findById(userDetails.getId());
        refreshToken.setUser(user);
        var savedToken = refreshTokenService.save(refreshToken);
        var cookie = tokenUtils.setCookie(savedToken.getToken());
        var occupation = user.getOccupation() != null ? user.getOccupation() : "";
        var response = new AuthModel(token, user.getName(), user.getEmail(), user.getPhone(),
                authority.get().getAuthority(), occupation, user.getAvatar());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiSuccessResponse<>(response));
    }

    private ResponseEntity<?> handleRegister(UserPayload request) {
        userUtils.validationNewUser(request, false);
        var role = roleService.findByName(RoleName.ROLE_USER);
        var user = new User(request.getName(),
                request.getEmail().toLowerCase(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                true, role);
        user.setOccupation(request.getOccupation());
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SuccessResponse(true, "Tạo tài khoản thành công"));
    }


}
