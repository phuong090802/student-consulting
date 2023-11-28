package com.ute.studentconsulting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.InternalServerErrorException;
import com.ute.studentconsulting.mail.service.MailService;
import com.ute.studentconsulting.payload.request.ResetPasswordRequest;
import com.ute.studentconsulting.model.SimpleMailModel;
import com.ute.studentconsulting.payload.request.ForgotPasswordRequest;
import com.ute.studentconsulting.payload.request.UpdatePasswordRequest;
import com.ute.studentconsulting.payload.response.ErrorResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.RefreshTokenService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordController {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private final UserService userService;

    @Value("${frontend.local.url}")
    private String localFrontendUrl;
    private final MailService mailService;

    @Value("${student-consulting.app.reset-password-expires-ms}")
    private Long resetPasswordExpiresMs;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthUtils authUtils;

    @PostMapping("/reset/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable("token") String token,
                                           @RequestBody ResetPasswordRequest request) {
        byte[] bytes;
        try {
            bytes = Base64.getUrlDecoder().decode(token);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Yêu cầu không hợp lệ", e.getMessage(), 10064);
        }
        var jsonValue = new String(bytes, StandardCharsets.UTF_8);
        String decodedToken;
        try {
            decodedToken = objectMapper.readValue(jsonValue, String.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Yêu cầu không hợp lệ", e.getMessage(), 10063);
        }
        var user = userService.findByResetPasswordTokenAndResetPasswordExpireAfter(decodedToken, new Date());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Mật khẩu xác nhận không khớp",
                    "Kiểm tra lại mật khẩu và xác nhận mật khẩu", 10057));
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpire(null);
        userService.save(user);
        refreshTokenService.deleteByUser(user);
        return ResponseEntity.ok(new SuccessResponse("Đặt lại mật khẩu thành công"));
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return handleForgotPassword(request);
    }

    private ResponseEntity<?> handleForgotPassword(ForgotPasswordRequest request) {
        validationEmailFormatter(request.getEmail());
        var user = userService.findByEmailAndEnabledIs(request.getEmail(), true);
        var resetPasswordToken = UUID.randomUUID().toString();
        var encodeToken = generateResetPasswordToken(resetPasswordToken);
        var url = "%s/password/reset/%s".formatted(localFrontendUrl, encodeToken);
        user.setResetPasswordToken(resetPasswordToken);
        user.setResetPasswordExpire(new Date(new Date().getTime() + resetPasswordExpiresMs));
        userService.save(user);
        var subject = "Đặt lại mật khẩu";
        mailService.sendSimpleMail(new SimpleMailModel(user.getEmail(), subject, url));
        return ResponseEntity.ok(new SuccessResponse("Đã gửi mail đến %s".formatted(user.getEmail())));
    }

    private String generateResetPasswordToken(String resetPasswordToken) {
        String json;
        try {
            json = objectMapper.writeValueAsString(resetPasswordToken);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Lỗi server", e.getMessage(), 10065);
        }
        var token = json.getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().encodeToString(token);
    }

    private void validationEmailFormatter(String email) {
        var patternEmail = Pattern.compile(EMAIL_REGEX);
        var matcherEmail = patternEmail.matcher(email.trim());
        if (!matcherEmail.matches()) {
            throw new BadRequestException("Email không đúng định dạng", "Email %s không đúng định dạng".formatted(email), 10059);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'COUNSELLOR', 'DEPARTMENT_HEAD', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        var user = authUtils.getCurrentUser();
        var isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Mật khẩu hiện tại không chính xác",
                    "Kiểm tra lại mật khẩu hiện tại không chính xác", 10068));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Mật khẩu xác nhận không khớp",
                    "Kiểm tra lại mật khẩu và xác nhận mật khẩu", 10069));
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        refreshTokenService.deleteByUser(user);
        return ResponseEntity.ok(new SuccessResponse("Đổi mật khẩu thành công"));
    }
}
