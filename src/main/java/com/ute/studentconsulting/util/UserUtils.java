package com.ute.studentconsulting.util;

import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ConflictException;
import com.ute.studentconsulting.model.StaffModel;
import com.ute.studentconsulting.model.UserModel;
import com.ute.studentconsulting.payload.UserPayload;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUtils {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^(\\+84|84|0)(3[2-9]|5[689]|7[06-9]|8[1-9]|9[0-9])\\d{7}$";
    private final UserService userService;

    public void validationNewUser(UserPayload request, boolean grant) {
        if (!StringUtils.hasText(request.getName())) {
            throw new BadRequestException("Họ và tên không thể để trống", "Họ và tên bị trống", 10042);
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new BadRequestException("Email không thể để trống", "Email bị trống", 10043);
        }
        if (!StringUtils.hasText(request.getPhone())) {
            throw new BadRequestException("Số điện thoại không thể để trống", "Số điện thoại bị trống", 10044);
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("Mật khẩu không thể để trống", "Mật khẩu bị trống", 10045);
        }
        if (!grant && !StringUtils.hasText(request.getOccupation())) {
            throw new BadRequestException("Nghề nghiệp không thể để trống", "Nghề nghiệp bị trống", 10046);
        }
        if (grant && !StringUtils.hasText(request.getRole())) {
            throw new BadRequestException("Quyền truy cập không thể để trống", "Quyền truy cập bị trống", 10047);
        }

        var patternEmail = Pattern.compile(EMAIL_REGEX);
        var email = request.getEmail().trim();
        var matcherEmail = patternEmail.matcher(email);
        if (!matcherEmail.matches()) {
            throw new BadRequestException("Email không đúng định dạng", "Email %s không đúng định dạng".formatted(email), 10048);
        }

        var patternPhone = Pattern.compile(PHONE_REGEX);
        var phone = request.getPhone().trim();
        var matcherPhone = patternPhone.matcher(phone);
        if (!matcherPhone.matches()) {
            throw new BadRequestException("Số điện thoại không đúng định dạng",
                    "Số điện thoại %s không đúng định dạng".formatted(phone), 10049);
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại", "Email %s đã tồn tại trong hệ thống".formatted(email), 10050);
        }
        if (userService.existsByPhone(request.getPhone())) {
            throw new ConflictException("Số điện thoại đã tồn tại", "Số điện thoại %s đã tồn tại trong hệ thống".formatted(phone), 10051);
        }
    }

    public void validationGrantAccount(UserPayload request) {
        validationNewUser(request, true);
    }

    public List<UserModel> mapUserPageToUserModels(Page<User> userPage) {
        return userPage.getContent().stream().map(user -> new UserModel(
                user.getId(), user.getName(),
                user.getEmail(), user.getPhone(),
                user.getAvatar(), user.getEnabled(),
                user.getOccupation(), user.getRole().getName().name())).toList();
    }

    public List<StaffModel> mapUserPageToStaffModels(Page<User> userPage) {
        return userPage.getContent().stream().map(user -> new StaffModel(
                user.getId(), user.getName(),
                user.getEmail(), user.getAvatar())).toList();
    }
}
