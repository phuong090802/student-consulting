package com.ute.studentconsulting.utility;

import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ConflictException;
import com.ute.studentconsulting.model.StaffModel;
import com.ute.studentconsulting.model.UserModel;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserUtility {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^(0\\d{9})|(\\+84\\d{8})$";
    private final UserService userService;

    public void validationNewUser(UserPayload request, boolean grant) {
        var fullName = request.getName().trim();
        if (fullName.isEmpty()) {
            throw new BadRequestException("Tên người dùng không thể để trống",
                    "Tên người dùng được nhập đang bị trống", 10022);
        }

        var email = request.getEmail().trim();
        if (email.isEmpty()) {
            throw new BadRequestException("Email không thể để trống",
                    "Email được nhập hiện tại đang bị trống", 10023);
        }

        var phone = request.getPhone().trim();
        if (phone.isEmpty()) {
            throw new BadRequestException("Số điện thoại không thể để trống",
                    "Số điện thoại được nhập hiện tại đang bị trống", 10024);
        }

        var password = request.getPassword().trim();
        if (password.isEmpty()) {
            throw new BadRequestException("Mật khẩu không thể để trống",
                    "Mật khẩu được nhập hiện tại đang bị trống", 10025);
        }

        var occupation = request.getOccupation();
        if (!grant && occupation.isEmpty()) {
            throw new BadRequestException("Nghề nghiệp không thể để trống",
                    "Nghề nghiệp được nhập hiện tại đang bị trống", 10026);
        }

        var role = request.getRole();
        if (role != null && role.isEmpty()) {
            throw new BadRequestException("Quyền truy cập không thể để trống",
                    "Quyền truy cập được nhập hiện tại đang bị trống", 10027);
        }

        var patternEmail = Pattern.compile(EMAIL_REGEX);
        var matcherEmail = patternEmail.matcher(email);
        if (!matcherEmail.matches()) {
            throw new BadRequestException("Email không đúng định dạng",
                    "Email " + email + " không đúng định dạng", 10028);
        }

        var patternPhone = Pattern.compile(PHONE_REGEX);
        var matcherPhone = patternPhone.matcher(phone);
        if (!matcherPhone.matches()) {
            throw new BadRequestException("Số điện thoại không đúng định dạng",
                    "Số điện thoại " + phone + " không đúng định dạng", 10029);
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại", "Email " + email + " đã tồn tại trong hệ thống", 10030);
        }

        if (userService.existsByPhone(request.getPhone())) {
            throw new ConflictException("Số điện thoại đã tồn tại", "Số điện thoại " + phone + " đã tồn tại trong hệ thống", 10031);
        }
    }

    public void validationGrantAccount(UserPayload request) {
        validationNewUser(request, true);
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
