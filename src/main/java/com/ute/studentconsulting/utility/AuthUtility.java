package com.ute.studentconsulting.utility;

import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.AppException;
import com.ute.studentconsulting.security.service.impl.UserDetailsImpl;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthUtility {
    private final UserService userService;

    public User getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            return userService.findById(((UserDetailsImpl) principal).getId());
        }
        throw new AppException("Lỗi lấy người dùng hiện tại");
    }
}
