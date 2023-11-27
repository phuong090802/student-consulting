package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.*;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ServerException;
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

    @GetMapping("/users/{id}/departments")
    public ResponseEntity<?> getDepartmentOfUser(
            @PathVariable("id") String userId) {
        try {
            return handleGetDepartmentOfUser(userId);
        } catch (Exception e) {
            log.error("Lỗi lấy phòng ban của người dùng: {}", e.getMessage());
            throw new ServerException("Lỗi lấy phòng ban của người dùng", e.getMessage(), 10033);
        }
    }

    private ResponseEntity<?> handleGetDepartmentOfUser(String id) {
        var user = userService.findById(id);
        var department = user.getDepartment();
        if (department == null) {
            throw new BadRequestException("Người dùng không nằm trong phòng ban nào cả", "Thông tin user: " + user, 10034);
        }
        return ResponseEntity.ok(new ApiResponse<>(true,
                new DepartmentPayload(
                        department.getId(),
                        department.getName(),
                        department.getDescription(),
                        department.getStatus()
                )));
    }

    @GetMapping("/users/department-is-null")
    public ResponseEntity<?> getCounsellorDepartmentIsNull(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort
    ) {
        try {
            return handleGetCounsellorDepartmentIsNull(value, page, size, sort);
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách tư vấn viên chưa có phòng ban: {}", e.getMessage());
            throw new ServerException("Lỗi lấy danh sách tư vấn viên chưa có phòng ban", e.getMessage(), 10035);
        }
    }

    private ResponseEntity<?> handleGetCounsellorDepartmentIsNull(String value, int page, int size, String[] sort) {
        var roleCounsellor = roleService.findByName(RoleName.ROLE_COUNSELLOR);

        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var userPage = (value == null) ?
                userService.findAllByRoleIsAndDepartmentIsNullAndEnabledIsTrue(pageable, roleCounsellor)
                : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNullAndEnabledIsTrue(
                value, roleCounsellor, pageable
        );
        var staffs = userUtility.mapUserPageToStaffModels(userPage);
        var response =
                new PaginationModel<>(
                        staffs,
                        userPage.getNumber(),
                        userPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


    @PatchMapping("/department-head/users/{userId}/departments/{departmentId}")
    public ResponseEntity<?> updateDepartmentHeadOfDepartment(
            @PathVariable("userId") String userId,
            @PathVariable("departmentId") String departmentId) {
        try {
            return handleUpdateDepartmentHeadOfDepartment(userId, departmentId);
        } catch (Exception e) {
            log.error("Lỗi thay đổi trưởng phòng ban: {}", e.getMessage());
            throw new ServerException("Lỗi thay đổi trưởng phòng ban", e.getMessage(), 10036);

        }
    }

    private ResponseEntity<?> handleUpdateDepartmentHeadOfDepartment(String userId, String departmentId) {
        var department = departmentService.findByIdAndStatusIsTrue(departmentId);
        var roleDepartmentHead = roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
        var oldDepartmentHead = userService.findByDepartmentAndRole(department, roleDepartmentHead);
        if (oldDepartmentHead != null) {
            var roleCounsellor = roleService.findByName(RoleName.ROLE_COUNSELLOR);
            oldDepartmentHead.setRole(roleCounsellor);
            userService.save(oldDepartmentHead);
        }
        var newDepartmentHead = userService.findByIdAndEnabledIsTrue(userId);
        newDepartmentHead.setRole(roleDepartmentHead);
        userService.save(newDepartmentHead);
        return ResponseEntity.ok(new MessageResponse(true, "Đổi trưởng phòng ban thành công"));
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
            throw new ServerException("Lỗi lấy nhân viên trong phòng ban", e.getMessage(), 10037);
        }
    }

    private ResponseEntity<?> handleGetUsersInDepartment(String id, String value, int page, int size, String[] sort) {
        var department = departmentService.findByIdAndStatusIsTrue(id);
        var roleDepartmentHead = roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
        var departmentHead = userService.findByDepartmentAndRole(department, roleDepartmentHead);
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));

        Page<User> userPage;
        if (departmentHead != null) {
            userPage = (value == null) ?
                    userService.findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue(pageable, department, departmentHead.getId())
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNotAndEnabledIsTrue(
                    value, department, departmentHead.getId(), pageable
            );
        } else {
            userPage = (value == null) ?
                    userService.findAllByDepartmentIsAndEnabledIsTrue(pageable, department)
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndEnabledIsTrue(
                    value, department, pageable
            );
        }

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
                (departmentHead != null) ? new StaffModel(
                        departmentHead.getId(),
                        departmentHead.getName(),
                        departmentHead.getEmail(),
                        departmentHead.getAvatar()
                ) : null);
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
            throw new ServerException("Lỗi thêm tư vấn viên vào phòng ban", e.getMessage(), 10038);
        }
    }

    private ResponseEntity<?> handleAddUserToDepartment(String userId, String departmentId) {
        var department = departmentService.findByIdAndStatusIsTrue(departmentId);
        var user = userService.findById(userId);
        if (user.getRole().getName().equals(RoleName.ROLE_COUNSELLOR)) {
            user.setDepartment(department);
            userService.save(user);
            return ResponseEntity.ok(new MessageResponse(true, "Thêm tư vấn viên vào phòng ban thành công"));
        }
        throw new ServerException("Lỗi thêm tư vấn viên vào phòng ban",
                "Lỗi người được thêm vào phòng ban không phải là tư vấn viên, thông tin người dùng: " + user, 10039);
    }


    @PatchMapping("/users/{id}")
    public ResponseEntity<?> patchAccessibilityUser(@PathVariable("id") String id) {
        try {
            return handlePatchAccessibilityUser(id);
        } catch (Exception e) {
            log.error("Lỗi khóa/mở khóa tài khoản: {}", e.getMessage());
            throw new ServerException("Lỗi khóa/mở khóa tài khoản", e.getMessage(), 10040);

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
            throw new ServerException("Lỗi tìm kiếm người dùng", e.getMessage(), 10041);
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
            log.error("Lỗi sắp xếp, lọc, phân trang ,tìm kiếm người dùng: {}", e.getMessage());
            throw new ServerException("Lỗi sắp xếp, lọc, phân trang ,tìm kiếm người dùng", e.getMessage(), 10042);
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
            log.error("Lỗi thêm nhân viên: {}", e.getMessage());
            throw new ServerException("Lỗi thêm nhân viên", e.getMessage(), 10043);
        }
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        userUtility.validationGrantAccount(request);

        var validRoles = List.of("counsellor", "supervisor");
        var roleNameMap = Map.of("counsellor", RoleName.ROLE_COUNSELLOR, "supervisor", RoleName.ROLE_SUPERVISOR);

        if (!validRoles.contains(request.getRole())) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Giá trị " + request.getRole() + " không hợp lệ", 10044);
        }

        var role = roleService.findByName(roleNameMap.get(request.getRole()));
        if (role == null) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Giá trị " + request.getRole() + " không hợp lệ", 10044);
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
                new MessageResponse(true, "Tạo tài khoản thành công"),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/fields/{id}")
    public ResponseEntity<?> deleteField(@PathVariable("id") String id) {
        try {
            return handleDeleteField(id);
        } catch (Exception e) {
            log.error("Lỗi xóa lĩnh vực: {}", e.getMessage());
            throw new ServerException("Lỗi xóa lĩnh vực", e.getMessage(), 10045);
        }
    }

    private ResponseEntity<?> handleDeleteField(String id) {
        fieldService.deleteById(id);
        return new ResponseEntity<>(
                new MessageResponse(true, "Xóa lĩnh vực thành công"),
                HttpStatus.OK);
    }

    @PostMapping("/fields")
    public ResponseEntity<?> createField(@RequestBody FieldPayload request) {
        try {
            return handleCreateField(request);
        } catch (Exception e) {
            log.error("Lỗi thêm lĩnh vực: {}", e.getMessage());
            throw new ServerException("Lỗi thêm lĩnh vực", e.getMessage(), 10046);
        }
    }

    @PutMapping("/fields/{id}")
    public ResponseEntity<?> updateField(@PathVariable("id") String id, @RequestBody FieldPayload request) {
        try {
            return handleUpdateField(id, request);
        } catch (Exception e) {
            log.error("Lỗi cập nhật lĩnh vực: {}", e.getMessage());
            throw new ServerException("Lỗi cập nhật lĩnh vực", e.getMessage(), 10046);
        }
    }

    private ResponseEntity<?> handleCreateField(FieldPayload request) {
        validationCreateField(request);
        var fields = new Field(
                request.getName()
        );
        fieldService.save(fields);
        return new ResponseEntity<>(
                new MessageResponse(true, "Thêm lĩnh vực thành công"),
                HttpStatus.CREATED);
    }

    private ResponseEntity<?> handleUpdateField(String id, FieldPayload request) {
        validationUpdateField(id, request);
        var field = fieldService.findById(id);
        field.setName(request.getName());
        fieldService.save(field);
        return new ResponseEntity<>(
                new MessageResponse(true, "Cập nhật lĩnh vực thành công"),
                HttpStatus.OK);

    }

    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentPayload request) {
        try {
            return handleCreateDepartment(request);
        } catch (Exception e) {
            log.error("Lỗi tạo khoa: {}", e.getMessage());
            throw new ServerException("Lỗi tạo khoa", e.getMessage(), 10047);
        }
    }


    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") String id, @RequestBody DepartmentPayload request) {
        try {
            return handleUpdateDepartment(id, request);
        } catch (Exception e) {
            log.error("Lỗi cập nhật khoa: {}", e.getMessage());
            throw new ServerException("Lỗi cập nhật khoa", e.getMessage(), 10047);
        }
    }

    @PatchMapping("/departments/{id}")
    public ResponseEntity<?> patchStatusDepartment(@PathVariable("id") String id) {
        try {
            return handlePatchStatusDepartment(id);
        } catch (Exception e) {
            log.error("Lỗi cập nhật trạng thái khoa: {}", e.getMessage());
            throw new ServerException("Lỗi cập nhật trạng thái khoa", e.getMessage(), 10048);
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
        validationUpdateDepartment(id, request);
        var department = departmentService.findById(id);
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        departmentService.save(department);
        return new ResponseEntity<>(
                new MessageResponse(true, "Cập nhật khoa thành công"),
                HttpStatus.OK);
    }

    private ResponseEntity<?> handleCreateDepartment(DepartmentPayload request) {
        validationCreateDepartment(request);
        var department = new Department(
                request.getName(),
                request.getDescription()
        );
        department.setStatus(true);
        departmentService.save(department);
        return new ResponseEntity<>(
                new MessageResponse(true, "Thêm khoa thành công"),
                HttpStatus.CREATED);
    }

    private void validationCreateDepartment(DepartmentPayload request) {
        var name = request.getName().trim();
        if (name.isEmpty()) {

            throw new BadRequestException("Tên khoa không để thể trống", "Tên khoa hiện được nhập đang trống", 10049);
        }
        if (departmentService.existsByName(name)) {
            throw new BadRequestException("Khoa đã tồn tại", "Tên khoa" + name + " đã tồn tại", 10050);
        }
    }

    public void validationUpdateDepartment(String id, DepartmentPayload request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên khoa không để thể trống", "Tên khoa hiện được nhập đang trống", 10049);
        }

        if (departmentService.existsByNameAndIdIsNot(name, id)) {
            throw new BadRequestException("Khoa đã tồn tại", "Tên khoa" + name + " đã tồn tại", 10050);
        }
    }

    private void validationCreateField(FieldPayload request) {
        var name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực hiện được nhập đang trống", 10051);
        }
        if (fieldService.existsByName(name)) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực" + name + " đã tồn tại", 10052);
        }
    }

    public void validationUpdateField(String id, FieldPayload request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực hiện được nhập đang trống", 10051);
        }

        if (fieldService.existsByNameAndIdIsNot(name, id)) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực" + name + " đã tồn tại", 10052);
        }
    }
}
