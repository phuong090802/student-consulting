package com.ute.studentconsulting.util;

import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.UnauthorizedException;
import com.ute.studentconsulting.security.service.impl.UserDetailsImpl;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final UserService userService;

    public User getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            return userService.findById(((UserDetailsImpl) principal).getId());
        }
        throw new UnauthorizedException("Lỗi lấy người dùng hiện tại", "Lỗi lấy người dùng hiện tại thông qua token", 10011);
    }
}
