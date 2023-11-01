package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.*;
import com.ute.studentconsulting.exception.AppException;
import com.ute.studentconsulting.model.ErrorModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.model.StaffModel;
import com.ute.studentconsulting.model.UserModel;
import com.ute.studentconsulting.payloads.DepartmentPayload;
import com.ute.studentconsulting.payloads.FieldPayload;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.RoleService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.utility.SortUtility;
import com.ute.studentconsulting.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final DepartmentService departmentService;
    private final FieldService fieldService;
    private final UserUtility userUtility;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final SortUtility sortUtility;

    @PatchMapping("/department-head/users/{userId}/departments/{departmentId}")
    public ResponseEntity<?> updateDepartmentHeadOfDepartment(
            @PathVariable("userId") String userId,
            @PathVariable("departmentId") String departmentId) {
        try {
            return handleUpdateDepartmentHeadOfDepartment(userId, departmentId);
        } catch (Exception e) {
            log.error("Lỗi thay đổi trưởng phòng ban: {}", e.getMessage());
            throw new AppException("Lỗi thay đổi trưởng phòng ban: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleUpdateDepartmentHeadOfDepartment(String userId, String departmentId) {
        var department = departmentService.findByIdAndStatusIsTrue(departmentId);
        var roleDepartmentHead = roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
        var oldDepartmentHead = userService.findByDepartmentAndRole(department, roleDepartmentHead);
        var roleCounsellor = roleService.findByName(RoleName.ROLE_COUNSELLOR);
        oldDepartmentHead.setRole(roleCounsellor);
        userService.save(oldDepartmentHead);
        var newDepartmentHead = userService.findByIdAndEnabledIsTrue(userId);
        newDepartmentHead.setRole(roleDepartmentHead);
        userService.save(newDepartmentHead);
        return ResponseEntity.ok(new MessageResponse(true, "Đổi trưởng phòng ban thành công."));
    }

    @GetMapping("/users/departments/{id}")
    public ResponseEntity<?> getUsersInDepartment(
            @PathVariable("id") String id,
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        try {
            return handleGetUsersInDepartment(id, value, page, size, sort);
        } catch (Exception e) {
            log.error("Lỗi lấy nhân viên trong phòng ban: {}", e.getMessage());
            throw new AppException("Lỗi lấy nhân viên trong phòng ban: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleGetUsersInDepartment(String id, String value, int page, int size, String[] sort) {
        var department = departmentService.findByIdAndStatusIsTrue(id);
        var roleDepartmentHead = roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
        var departmentHead = userService.findByDepartmentAndRole(department, roleDepartmentHead);

        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var userPage = (value == null) ?
                userService.findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue(pageable, department, departmentHead.getId())
                : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNotAndEnabledIsTrue(
                value, department, departmentHead.getId(), pageable
        );
        var staffs = userUtility.mapUserPageToStaffModels(userPage);
        var items =
                new PaginationModel<>(
                        staffs,
                        userPage.getNumber(),
                        userPage.getTotalPages()
                );
        var response = new HashMap<>();
        response.put("counsellor", items);
        response.put("departmentHead",
                new StaffModel(
                        departmentHead.getId(),
                        departmentHead.getName(),
                        departmentHead.getEmail(),
                        departmentHead.getAvatar()));
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


    @PatchMapping("/users/{userId}/departments/{departmentId}")
    public ResponseEntity<?> addUserToDepartment(
            @PathVariable("userId") String userId,
            @PathVariable("departmentId") String departmentId) {
        try {
            return handleAddUserToDepartment(userId, departmentId);
        } catch (Exception e) {
            log.error("Lỗi thêm tư vấn viên vào phòng ban: {}", e.getMessage());
            throw new AppException("Lỗi thêm tư vấn viên vào phòng ban: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleAddUserToDepartment(String userId, String departmentId) {
        var department = departmentService.findByIdAndStatusIsTrue(departmentId);
        var user = userService.findById(userId);
        if (user.getRole().getName().equals(RoleName.ROLE_COUNSELLOR)) {
            user.setDepartment(department);
            userService.save(user);
            return ResponseEntity.ok(new MessageResponse(true, "Thêm tư vấn viên vào phòng ban thành công."));
        }
        return ResponseEntity.ok(new MessageResponse(false, "Người được thêm vào phòng ban không phải là tư vấn viên."));
    }


    @PatchMapping("/users/{id}")
    public ResponseEntity<?> patchAccessibilityUser(@PathVariable("id") String id) {
        try {
            return handlePatchAccessibilityUser(id);
        } catch (Exception e) {
            log.error("Khóa/mở khóa tài khoản thất bại: {}", e.getMessage());
            throw new AppException("Khóa/mở khóa tài khoản thất bại: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handlePatchAccessibilityUser(String id) {
        var user = userService.findById(id);
        var enabled = !user.getEnabled();
        user.setEnabled(enabled);
        userService.save(user);
        return new ResponseEntity<>(
                new ApiResponse<>(true, enabled),
                HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") String id) {
        try {
            return handleGetUser(id);
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm người dùng: {}", e.getMessage());
            throw new AppException("Lỗi tìm kiếm người dùng: " + e.getMessage());
        }
    }


    private ResponseEntity<?> handleGetUser(String id) {
        var admin = roleService.findByName(RoleName.ROLE_ADMIN);
        var user = userService.findByIdAndRoleIsNot(id, admin);
        return ResponseEntity.ok(new ApiResponse<>(true, new UserModel(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getEnabled(),
                user.getOccupation(),
                user.getRole().getName().name())));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "role") String role,
            @RequestParam(defaultValue = "all", name = "status") String status,
            @RequestParam(defaultValue = "all", name = "occupation") String occupation) {
        try {
            return handleGetUsers(value, page, size, sort, role, status, occupation);
        } catch (Exception e) {
            log.error("Lỗi lọc, phân trang người dùng: {}", e.getMessage());
            throw new AppException("Lỗi lọc, phân trang người dùng:" + e.getMessage());
        }
    }

    private Role getRoleByName(String role) {
        return switch (role) {
            case "supervisor" -> roleService.findByName(RoleName.ROLE_SUPERVISOR);
            case "departmentHead" -> roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
            case "counsellor" -> roleService.findByName(RoleName.ROLE_COUNSELLOR);
            case "user" -> roleService.findByName(RoleName.ROLE_USER);
            default -> null;
        };
    }

    // status = all, role = all
    private Page<User> getUserStatusIsAllAndRoleIsAllAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNot(pageable, admin);
            case "others" -> userService
                    .findAllByRoleIsNotAndOccupationNotIn(pageable, admin, occupations);
            default -> userService
                    .findAllByRoleIsNotAndOccupationEqualsIgnoreCase(pageable, admin, occupation);
        };
    }

    // status = all, role = all and search
    private Page<User> getUserStatusIsAllAndRoleIsAllAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, Role admin, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot
                            (value, pageable, admin);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
                            (value, occupations, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
                            (value, pageable, admin, occupation);
        };
    }

    // status = all
    private Page<User> getUserStatusIsAllAndRoleIsAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndRoleIs(pageable, admin, role);
            case "others" -> userService
                    .findAllByRoleIsNotAndRoleIsAndOccupationNotIn(pageable, admin, role, occupations);
            default -> userService
                    .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase(pageable, admin, role, occupation);
        };
    }

    // status = all and search
    private Page<User> getUserStatusIsAllAndRoleIsAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, Role admin, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
                            (value, pageable, admin, role);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
                            (value, occupations, role, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
                            (value, pageable, admin, role, occupation);
        };
    }

    // status = enabled, role = all
    private Page<User> getUserStatusIsEnabledAndRoleIsAllAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndEnabledIsTrue(pageable, admin);
            case "others" -> userService
                    .findAllByRoleIsNotAndOccupationNotInAndEnabledIsTrue
                            (pageable, admin, occupations);
            default -> userService
                    .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                            (pageable, admin, occupation);
        };
    }

    // status = enabled, role = all and search
    private Page<User> getUserStatusIsEnabledAndRoleIsAllAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsTrue
                            (value, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsTrue
                            (value, occupations, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                            (value, occupation, pageable);
        };
    }

    // status = enabled
    private Page<User> getUserStatusIsEnabledAndRoleIsAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndRoleIsAndEnabledIsTrue(pageable, admin, role);
            case "others" -> userService
                    .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsTrue
                            (role, occupations, pageable);

            default -> userService
                    .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                            (pageable, admin, role, occupation);
        };
    }

    // status = enabled and search
    private Page<User> getUserStatusIsEnabledAndRoleIsAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsTrue
                            (value, role, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsTrue
                            (value, occupations, role, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                            (value, role, occupation, pageable);
        };
    }

    // status = disabled, role = all
    private Page<User> getUserStatusIsDisabledAndRoleIsAllAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndEnabledIsFalse(pageable, admin);
            case "others" -> userService
                    .findAllByRoleIsNotAndOccupationNotInAndEnabledIsFalse(pageable, admin, occupations);
            default -> userService
                    .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse(pageable, admin, occupation);
        };
    }

    // status = disabled, role = all and search
    private Page<User> getUserStatusIsDisabledAndRoleIsAllAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsFalse
                            (value, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsFalse
                            (value, occupations, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                            (value, occupation, pageable);
        };
    }

    // status = disabled, role
    private Page<User> getUserStatusIsDisabledAndRoleIsAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndRoleIsAndEnabledIsFalse(pageable, admin, role);
            case "others" -> userService
                    .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsFalse
                            (role, occupations, pageable);
            default -> userService
                    .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                            (pageable, admin, role, occupation);
        };
    }


    // status = disabled, role and search
    private Page<User> getUserStatusIsDisabledAndRoleIsAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsFalse
                            (value, role, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsFalse
                            (value, occupations, role, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                            (value, role, occupation, pageable);
        };
    }


    private Page<User> getUserStatusIsAll
            (Role role, String value, String occupation, Pageable pageable, Role admin, List<String> occupations) {
        if (role == null) {
            return (value == null)
                    ? getUserStatusIsAllAndRoleIsAllAndOccupationIs
                    (occupation, pageable, admin, occupations)
                    : getUserStatusIsAllAndRoleIsAllAndOccupationIsAndSearch
                    (occupation, value, pageable, admin, occupations);
        }
        return (value == null)
                ? getUserStatusIsAllAndRoleIsAndOccupationIs
                (occupation, pageable, admin, occupations, role)
                : getUserStatusIsAllAndRoleIsAndOccupationIsAndSearch
                (occupation, value, pageable, admin, occupations, role);
    }

    private Page<User> getUserStatusIsEnabled
            (Role role, String value, String occupation, Pageable pageable, Role admin, List<String> occupations) {
        if (role == null) {
            return (value == null)
                    ? getUserStatusIsEnabledAndRoleIsAllAndOccupationIs
                    (occupation, pageable, admin, occupations)
                    : getUserStatusIsEnabledAndRoleIsAllAndOccupationIsAndSearch
                    (occupation, value, pageable, occupations);
        }
        return (value == null)
                ? getUserStatusIsEnabledAndRoleIsAndOccupationIs
                (occupation, pageable, admin, occupations, role)
                : getUserStatusIsEnabledAndRoleIsAndOccupationIsAndSearch
                (occupation, value, pageable, occupations, role);
    }

    private Page<User> getUserStatusIsDisabled
            (Role role, String value, String occupation, Pageable pageable, Role admin, List<String> occupations) {
        if (role == null) {
            return (value == null)
                    ? getUserStatusIsDisabledAndRoleIsAllAndOccupationIs
                    (occupation, pageable, admin, occupations)
                    : getUserStatusIsDisabledAndRoleIsAllAndOccupationIsAndSearch
                    (occupation, value, pageable, occupations);
        }
        return (value == null)
                ? getUserStatusIsDisabledAndRoleIsAndOccupationIs
                (occupation, pageable, admin, occupations, role)
                : getUserStatusIsDisabledAndRoleIsAndOccupationIsAndSearch
                (occupation, value, pageable, occupations, role);
    }

    private ResponseEntity<?> handleGetUsers
            (String value, int page, int size, String[] sort, String role, String status, String occupation) {
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var admin = roleService.findByName(RoleName.ROLE_ADMIN);
        var occupations = List.of("Sinh Viên", "Phụ Huynh", "Học Sinh", "Cựu Sinh Viên");
        var roleObj = getRoleByName(role);


        var userPage = switch (status) {
            case "enabled" -> getUserStatusIsEnabled(roleObj, value, occupation, pageable, admin, occupations);
            case "disabled" -> getUserStatusIsDisabled(roleObj, value, occupation, pageable, admin, occupations);
            default -> getUserStatusIsAll(roleObj, value, occupation, pageable, admin, occupations);
        };

        var users = userUtility.mapUserPageToUserModels(userPage);
        var response =
                new PaginationModel<>(
                        users,
                        userPage.getNumber(),
                        userPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserPayload request) {
        try {
            return handleCreateUser(request);
        } catch (Exception e) {
            log.error("Lỗi thêm người dùng trong hệ thống: {}", e.getMessage());
            throw new AppException("Lỗi thêm người thống: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        var error = userUtility.validationGrantAccount(request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }

        var validRoles = List.of("counsellor", "supervisor");
        var roleNameMap = Map.of("counsellor", RoleName.ROLE_COUNSELLOR, "supervisor", RoleName.ROLE_SUPERVISOR);

        if (!validRoles.contains(request.getRole())) {
            return new ResponseEntity<>(new MessageResponse(false, String.format("Quyền truy cập \"%s\" không hợp lệ.", request.getRole())), HttpStatus.BAD_REQUEST);
        }

        var role = roleService.findByName(roleNameMap.get(request.getRole()));
        if (role == null) {
            return new ResponseEntity<>(new MessageResponse(false, String.format("Quyền truy cập \"%s\" không hợp lệ.", request.getRole())), HttpStatus.BAD_REQUEST);
        }


        var user = new User(
                request.getName(),
                request.getEmail().toLowerCase(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                true,
                role);
        userService.save(user);
        return new ResponseEntity<>(
                new MessageResponse(true, "Tạo tài khoản thành công."),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/fields/{id}")
    public ResponseEntity<?> deleteField(@PathVariable("id") String id) {
        try {
            return handleDeleteField(id);
        } catch (Exception e) {
            log.error("Lỗi xóa lĩnh vực: {}", e.getMessage());
            throw new AppException("Lỗi xóa lĩnh vực: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleDeleteField(String id) {
        fieldService.deleteById(id);
        return new ResponseEntity<>(
                new MessageResponse(true, "Xóa lĩnh vực thành công."),
                HttpStatus.OK);
    }

    @PostMapping("/fields")
    public ResponseEntity<?> createField(@RequestBody FieldPayload request) {
        try {
            return handleCreateField(request);
        } catch (Exception e) {
            log.error("Lỗi thêm lĩnh vực: {}", e.getMessage());
            throw new AppException("Lỗi thêm lĩnh vực: " + e.getMessage());
        }
    }

    @PutMapping("/fields/{id}")
    public ResponseEntity<?> updateField(@PathVariable("id") String id, @RequestBody FieldPayload request) {
        try {
            return handleUpdateField(id, request);
        } catch (Exception e) {
            log.error("Lỗi cập nhật lĩnh vực: {}", e.getMessage());
            throw new AppException("Lỗi cập nhật lĩnh vực: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleCreateField(FieldPayload request) {
        var error = validationCreateField(request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }
        var fields = new Field(
                request.getName()
        );
        fieldService.save(fields);
        return new ResponseEntity<>(
                new MessageResponse(true, "Thêm lĩnh vực thành công."),
                HttpStatus.CREATED);
    }

    private ResponseEntity<?> handleUpdateField(String id, FieldPayload request) {
        var error = validationUpdateField(id, request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }

        var field = fieldService.findById(id);
        field.setName(request.getName());
        fieldService.save(field);
        return new ResponseEntity<>(
                new MessageResponse(true, "Cập nhật lĩnh vực thành công."),
                HttpStatus.OK);

    }

    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentPayload request) {
        try {
            return handleCreateDepartment(request);
        } catch (Exception e) {
            log.error("Lỗi tạo khoa: {}", e.getMessage());
            throw new AppException("Lỗi tạo khoa: " + e.getMessage());
        }
    }


    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") String id, @RequestBody DepartmentPayload request) {
        try {
            return handleUpdateDepartment(id, request);
        } catch (Exception e) {
            log.error("Lỗi cập nhật khoa: {}", e.getMessage());
            throw new AppException("Lỗi cập nhật khoa: " + e.getMessage());
        }
    }

    @PatchMapping("/departments/{id}")
    public ResponseEntity<?> patchStatusDepartment(@PathVariable("id") String id) {
        try {
            return handlePatchStatusDepartment(id);
        } catch (Exception e) {
            log.error("Lỗi cập nhật trạng thái khoa: {}", e.getMessage());
            throw new AppException("Lỗi cập nhật trạng thái khoa: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handlePatchStatusDepartment(String id) {
        var department = departmentService.findById(id);
        var status = !department.getStatus();
        department.setStatus(status);
        departmentService.save(department);
        return new ResponseEntity<>(
                new ApiResponse<>(true, status),
                HttpStatus.OK);
    }

    private ResponseEntity<?> handleUpdateDepartment(String id, DepartmentPayload request) {
        var error = validationUpdateDepartment(id, request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }

        var department = departmentService.findById(id);
        department.setName(request.getName());
        department.setDescription(request.getDescription());

        departmentService.save(department);
        return new ResponseEntity<>(
                new MessageResponse(true, "Cập nhật khoa thành công."),
                HttpStatus.OK);
    }

    private ResponseEntity<?> handleCreateDepartment(DepartmentPayload request) {
        var error = validationCreateDepartment(request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }
        var department = new Department(
                request.getName(),
                request.getDescription()
        );
        department.setStatus(request.getStatus());
        departmentService.save(department);
        return new ResponseEntity<>(
                new MessageResponse(true, "Thêm khoa thành công."),
                HttpStatus.CREATED);
    }

    private ErrorModel validationCreateDepartment(DepartmentPayload request) {
        var name = request.getName().trim();
        if (name.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Tên khoa không thể để trống.");
        }
        if (departmentService.existsByName(name)) {
            return new ErrorModel(HttpStatus.CONFLICT, "khoa đã tồn tại.");
        }
        return null;
    }

    public ErrorModel validationUpdateDepartment(String id, DepartmentPayload request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Tên khoa không để thể trống.");
        } else if (departmentService.existsByNameAndIdIsNot(name, id)) {
            return new ErrorModel(HttpStatus.CONFLICT, "khoa đã tồn tại.");
        }
        return null;
    }

    private ErrorModel validationCreateField(FieldPayload request) {
        var name = request.getName().trim();
        if (name.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Tên lĩnh vực không thể để trống.");
        }
        if (fieldService.existsByName(name)) {
            return new ErrorModel(HttpStatus.CONFLICT, "Lĩnh vực đã tồn tại.");
        }
        return null;
    }

    public ErrorModel validationUpdateField(String id, FieldPayload request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Tên lĩnh vực không thể để trống.");
        } else if (fieldService.existsByNameAndIdIsNot(name, id)) {
            return new ErrorModel(HttpStatus.CONFLICT, "Lĩnh vực đã tồn tại.");
        }
        return null;
    }

}
