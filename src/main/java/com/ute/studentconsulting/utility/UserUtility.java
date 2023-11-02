package com.ute.studentconsulting.utility;

import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.model.ErrorModel;
import com.ute.studentconsulting.model.StaffModel;
import com.ute.studentconsulting.model.UserModel;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserUtility {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^(0\\d{9})|(\\+84\\d{8})$";
    private final UserService userService;

    public ErrorModel validationNewUser(UserPayload request, boolean grant) {
        var fullName = request.getName().trim();
        if (fullName.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Tên người dùng không thể để trống");
        }

        var email = request.getEmail().trim();
        if (email.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Email không thể để trống");
        }

        var phone = request.getPhone().trim();
        if (phone.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Số điện thoại không thể để trống");
        }

        var password = request.getPassword().trim();
        if (password.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Mật khẩu không thể để trống");
        }

        var occupation = request.getOccupation();
        if (!grant && occupation.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Nghề nghiệp không thể để trống");
        }

        var role = request.getRole();
        if (role != null && role.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Quyền truy cập không thể để trống");
        }

        var patternEmail = Pattern.compile(EMAIL_REGEX);
        var matcherEmail = patternEmail.matcher(email);
        if (!matcherEmail.matches()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        var patternPhone = Pattern.compile(PHONE_REGEX);
        var matcherPhone = patternPhone.matcher(phone);
        if (!matcherPhone.matches()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (userService.existsByEmail(request.getEmail())) {
            return new ErrorModel(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        if (userService.existsByPhone(request.getPhone())) {
            return new ErrorModel(HttpStatus.CONFLICT, "Số điện thoại đã tồn tại");
        }
        return null;
    }

    public ErrorModel validationGrantAccount(UserPayload request) {
        var error = validationNewUser(request, true);
        if (error != null) {
            return error;
        }
        if (request.getRole() == null) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Quyền truy cập không thể để trống");
        }
        return null;
    }

    public List<UserModel> mapUserPageToUserModels(Page<User> userPage) {
        return userPage.getContent().stream().map(user ->
                new UserModel(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getAvatar(),
                        user.getEnabled(),
                        user.getOccupation(),
                        user.getRole().getName().name())
        ).toList();
    }

    public List<StaffModel> mapUserPageToStaffModels(Page<User> userPage) {
        return userPage.getContent().stream().map(user ->
                new StaffModel(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAvatar())
        ).toList();
    }
}
