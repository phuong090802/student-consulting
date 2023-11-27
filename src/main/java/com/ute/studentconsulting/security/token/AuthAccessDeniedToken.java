package com.ute.studentconsulting.security.token;

import com.ute.studentconsulting.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthAccessDeniedToken implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        log.error("Lỗi access denied: {}", accessDeniedException.getMessage());
        throw new ForbiddenException("Không đủ quyền truy cập", accessDeniedException.getMessage(), 10091);
    }
}
