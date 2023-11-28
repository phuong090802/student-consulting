package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.*;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.firebase.service.FireBaseService;
import com.ute.studentconsulting.model.NewsModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.model.StaffModel;
import com.ute.studentconsulting.model.UserModel;
import com.ute.studentconsulting.payload.UserPayload;
import com.ute.studentconsulting.payload.request.NewsRequest;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.ErrorResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.*;
import com.ute.studentconsulting.util.SortUtils;
import com.ute.studentconsulting.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final UserUtils userUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final SortUtils sortUtils;
    private final NewsService newsService;
    private final FireBaseService fireBaseService;

    @GetMapping("/news")
    public ResponseEntity<?> getAllNews(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort) {
        return handleGetAllNews(value, page, size, sort);
    }

    private ResponseEntity<?> handleGetAllNews(String value, int page, int size, String[] sort) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var allNewsPage = (value == null)
                ? newsService.findAll(pageable)
                : newsService.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(value, pageable);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var allNews = allNewsPage.stream().map(news ->
                new NewsModel(news.getId(), news.getTitle(), news.getContent(),
                        simpleDateFormat.format(news.getDate()), news.getFileUrl())).toList();
        var response = new PaginationModel<>(
                allNews, allNewsPage.getNumber(),
                allNewsPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }


    @DeleteMapping("/news/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable("id") String id) {
        return handleDeleteNews(id);
    }

    private ResponseEntity<?> handleDeleteNews(String id) {
        var news = newsService.findById(id);
        if (news.getBlobId() != null) {
            fireBaseService.deleteFile(news.getBlobId());
        }
        newsService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse("Xóa thông báo thành công"));
    }

    @PutMapping("/news/{id}")
    public ResponseEntity<?> updateNews(@PathVariable("id") String id, @RequestBody NewsRequest request) {
        return handleUpdateNews(id, request);
    }

    private ResponseEntity<?> handleUpdateNews(String id, NewsRequest request) {
        validationNews(request);
        var news = newsService.findById(id);
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        if (StringUtils.hasText(request.getBlobId()) && StringUtils.hasText(request.getFileUrl())) {
            if (news.getBlobId() != null) {
                fireBaseService.deleteFile(news.getBlobId());
            }
            news.setBlobId(request.getBlobId());
            news.setFileUrl(request.getFileUrl());
        }
        return ResponseEntity.ok(new SuccessResponse("Cập nhật thông báo thành công"));
    }

    @PostMapping("/news")
    public ResponseEntity<?> addNews(@RequestBody NewsRequest request) {
        return handleAddNews(request);
    }

    private ResponseEntity<?> handleAddNews(NewsRequest request) {
        validationNews(request);
        var news = new News(request.getTitle(), request.getContent(), new Date());
        if (StringUtils.hasText(request.getBlobId()) && StringUtils.hasText(request.getFileUrl())) {
            news.setBlobId(request.getBlobId());
            news.setFileUrl(request.getFileUrl());
        }
        newsService.save(news);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse("Đăng thông báo thành công"));
    }

    private void validationNews(NewsRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BadRequestException("Tiêu đề thông báo không thể trống", "Tên thông báo được nhập đang trống", 10082);
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new BadRequestException("Nội dung thông báo không thể trống", "Nội dung thông báo được nhập đang trống", 10083);
        }
    }

    @GetMapping("/users/{id}/departments")
    public ResponseEntity<?> getDepartmentOfUser(
            @PathVariable("id") String userId) {
        return handleGetDepartmentOfUser(userId);
    }

    private ResponseEntity<?> handleGetDepartmentOfUser(String id) {
        var user = userService.findById(id);
        var department = user.getDepartment();
        if (department == null) {
            throw new BadRequestException("Người dùng hiện không nằm trong phòng ban nào cả",
                    "Người dùng: %s hiện không nằm trong phòng ban nào cả".formatted(user), 10020);
        }
        return ResponseEntity.ok(new ApiSuccessResponse<>(department));
    }

    @GetMapping("/users/department-is-null")
    public ResponseEntity<?> getCounsellorDepartmentIsNull(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        return handleGetCounsellorDepartmentIsNull(value, page, size, sort);
    }

    private ResponseEntity<?> handleGetCounsellorDepartmentIsNull(String value, int page, int size, String[] sort) {
        var roleCounsellor = roleService.findByName(RoleName.ROLE_COUNSELLOR);
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var userPage = (value == null) ?
                userService.findAllByRoleIsAndDepartmentIsNull(pageable, roleCounsellor)
                : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNull(
                value, roleCounsellor, pageable
        );
        var staffs = userUtils.mapUserPageToStaffModels(userPage);
        var response = new PaginationModel<>(staffs,
                userPage.getNumber(), userPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }


    @PatchMapping("/department-head/users/{userId}/departments/{departmentId}")
    public ResponseEntity<?> updateDepartmentHeadOfDepartment(
            @PathVariable("userId") String userId,
            @PathVariable("departmentId") String departmentId) {
        return handleUpdateDepartmentHeadOfDepartment(userId, departmentId);
    }

    private ResponseEntity<?> handleUpdateDepartmentHeadOfDepartment(String userId, String departmentId) {
        var department = departmentService.findByIdAndStatusIs(departmentId, true);
        var roleDepartmentHead = roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
        var oldDepartmentHead = userService.findByDepartmentAndRole(department, roleDepartmentHead);
        if (oldDepartmentHead != null) {
            var roleCounsellor = roleService.findByName(RoleName.ROLE_COUNSELLOR);
            oldDepartmentHead.setRole(roleCounsellor);
            userService.save(oldDepartmentHead);
        }
        var newDepartmentHead = userService.findByIdAndEnabledIs(userId, true);
        newDepartmentHead.setRole(roleDepartmentHead);
        userService.save(newDepartmentHead);
        return ResponseEntity.ok(new SuccessResponse("Đổi trưởng phòng ban thành công"));
    }

    @GetMapping("/users/departments/{id}")
    public ResponseEntity<?> getUsersInDepartment(
            @PathVariable("id") String id,
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        return handleGetUsersInDepartment(id, value, page, size, sort);
    }

    private ResponseEntity<?> handleGetUsersInDepartment(String id, String value, int page, int size, String[] sort) {
        var department = departmentService.findByIdAndStatusIs(id, true);
        var roleDepartmentHead = roleService.findByName(RoleName.ROLE_DEPARTMENT_HEAD);
        var departmentHead = userService.findByDepartmentAndRole(department, roleDepartmentHead);
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<User> userPage;
        if (departmentHead != null) {
            userPage = (value == null) ?
                    userService.findAllByDepartmentIsAndIdIsNotAndEnabledIs(pageable, department, departmentHead.getId(), true)
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNot(
                    value, department, departmentHead.getId(), pageable
            );
        } else {
            userPage = (value == null) ?
                    userService.findAllByDepartmentIs(pageable, department)
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIs(
                    value, department, pageable);
        }

        var staffs = userUtils.mapUserPageToStaffModels(userPage);
        var items = new PaginationModel<>(
                staffs, userPage.getNumber(),
                userPage.getTotalPages());
        var response = new HashMap<>();
        response.put("counsellor", items);
        response.put("departmentHead",
                (departmentHead != null) ? new StaffModel(
                        departmentHead.getId(),
                        departmentHead.getName(),
                        departmentHead.getEmail(),
                        departmentHead.getAvatar()
                ) : null);
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }


    @PatchMapping("/users/{userId}/departments/{departmentId}")
    public ResponseEntity<?> addUserToDepartment(
            @PathVariable("userId") String userId,
            @PathVariable("departmentId") String departmentId) {
        return handleAddUserToDepartment(userId, departmentId);
    }

    private ResponseEntity<?> handleAddUserToDepartment(String userId, String departmentId) {
        var department = departmentService.findByIdAndStatusIs(departmentId, true);
        var user = userService.findById(userId);
        if (user.getRole().getName().equals(RoleName.ROLE_COUNSELLOR)) {
            user.setDepartment(department);
            userService.save(user);
            return ResponseEntity.ok(new SuccessResponse("Thêm tư vấn viên vào phòng ban thành công"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Lỗi thêm tư vấn viên vào phòng ban",
                "Lỗi người được thêm vào phòng ban không phải là tư vấn viên, thông tin người dùng: %s".formatted(user), 10021));
    }


    @PatchMapping("/users/{id}")
    public ResponseEntity<?> patchAccessibilityUser(@PathVariable("id") String id) {
        return handlePatchAccessibilityUser(id);
    }

    private ResponseEntity<?> handlePatchAccessibilityUser(String id) {
        var user = userService.findById(id);
        var enabled = !user.getEnabled();
        user.setEnabled(enabled);
        userService.save(user);
        return ResponseEntity.ok(new ApiSuccessResponse<>(enabled));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") String id) {
        return handleGetUser(id);
    }


    private ResponseEntity<?> handleGetUser(String id) {
        var admin = roleService.findByName(RoleName.ROLE_ADMIN);
        var user = userService.findByIdAndRoleIsNot(id, admin);
        return ResponseEntity.ok(new ApiSuccessResponse<>(new UserModel(
                user.getId(), user.getName(),
                user.getEmail(), user.getPhone(),
                user.getAvatar(), user.getEnabled(),
                user.getOccupation(), user.getRole().getName().name())));
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
        return handleGetUsers(value, page, size, sort, role, status, occupation);
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
                            (value, admin, occupations, pageable);
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
                            (value, admin, occupations, role, pageable);
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
                    .findAllByRoleIsNotAndEnabledIs(pageable, admin, true);
            case "others" -> userService
                    .findAllByRoleIsNotAndOccupationNotInAndEnabledIs
                            (pageable, admin, occupations, true);
            default -> userService
                    .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (pageable, admin, occupation, true);
        };
    }

    // status = enabled, role = all and search
    private Page<User> getUserStatusIsEnabledAndRoleIsAllAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations, Role admin) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIs
                            (value, admin, true, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIs
                            (value, occupations, true, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (value, occupation, true, pageable);
        };
    }

    // status = enabled
    private Page<User> getUserStatusIsEnabledAndRoleIsAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndRoleIsAndEnabledIs(pageable, admin, role, true);
            case "others" -> userService
                    .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIs
                            (role, occupations, true, pageable);

            default -> userService
                    .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (pageable, admin, role, occupation, true);
        };
    }

    // status = enabled and search
    private Page<User> getUserStatusIsEnabledAndRoleIsAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations, Role role, Role admin) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIs
                            (value, role, true, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIs
                            (value, admin, occupations, role, true, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (value, role, occupation, true, pageable);
        };
    }

    // status = disabled, role = all
    private Page<User> getUserStatusIsDisabledAndRoleIsAllAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndEnabledIs(pageable, admin, false);
            case "others" -> userService
                    .findAllByRoleIsNotAndOccupationNotInAndEnabledIs(pageable, admin, occupations, false);
            default -> userService
                    .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs(pageable, admin, occupation, false);
        };
    }

    // status = disabled, role = all and search
    private Page<User> getUserStatusIsDisabledAndRoleIsAllAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations, Role admin) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIs
                            (value, admin, false, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIs
                            (value, occupations, false, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (value, occupation, false, pageable);
        };
    }

    // status = disabled, role
    private Page<User> getUserStatusIsDisabledAndRoleIsAndOccupationIs
    (String occupation, Pageable pageable, Role admin, List<String> occupations, Role role) {
        return switch (occupation) {
            case "all" -> userService
                    .findAllByRoleIsNotAndRoleIsAndEnabledIs(pageable, admin, role, false);
            case "others" -> userService
                    .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIs
                            (role, occupations, false, pageable);
            default -> userService
                    .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (pageable, admin, role, occupation, false);
        };
    }


    // status = disabled, role and search
    private Page<User> getUserStatusIsDisabledAndRoleIsAndOccupationIsAndSearch
    (String occupation, String value, Pageable pageable, List<String> occupations, Role role, Role admin) {
        return switch (occupation) {
            case "all" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIs
                            (value, role, false, pageable);
            case "others" -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIs
                            (value, admin, occupations, role, false, pageable);
            default -> userService
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
                            (value, role, occupation, false, pageable);
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
                    (occupation, value, pageable, occupations, admin);
        }
        return (value == null)
                ? getUserStatusIsEnabledAndRoleIsAndOccupationIs
                (occupation, pageable, admin, occupations, role)
                : getUserStatusIsEnabledAndRoleIsAndOccupationIsAndSearch
                (occupation, value, pageable, occupations, role, admin);
    }

    private Page<User> getUserStatusIsDisabled
            (Role role, String value, String occupation, Pageable pageable, Role admin, List<String> occupations) {
        if (role == null) {
            return (value == null)
                    ? getUserStatusIsDisabledAndRoleIsAllAndOccupationIs
                    (occupation, pageable, admin, occupations)
                    : getUserStatusIsDisabledAndRoleIsAllAndOccupationIsAndSearch
                    (occupation, value, pageable, occupations, admin);
        }
        return (value == null)
                ? getUserStatusIsDisabledAndRoleIsAndOccupationIs
                (occupation, pageable, admin, occupations, role)
                : getUserStatusIsDisabledAndRoleIsAndOccupationIsAndSearch
                (occupation, value, pageable, occupations, role, admin);
    }

    private ResponseEntity<?> handleGetUsers
            (String value, int page, int size, String[] sort, String role, String status, String occupation) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var admin = roleService.findByName(RoleName.ROLE_ADMIN);
        var occupations = List.of("Sinh Viên", "Phụ Huynh", "Học Sinh", "Cựu Sinh Viên");
        var roleObj = getRoleByName(role);

        var userPage = switch (status) {
            case "enabled" -> getUserStatusIsEnabled(roleObj, value, occupation, pageable, admin, occupations);
            case "disabled" -> getUserStatusIsDisabled(roleObj, value, occupation, pageable, admin, occupations);
            default -> getUserStatusIsAll(roleObj, value, occupation, pageable, admin, occupations);
        };

        var users = userUtils.mapUserPageToUserModels(userPage);
        var response = new PaginationModel<>(users, userPage.getNumber(),
                userPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserPayload request) {
        return handleCreateUser(request);
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        userUtils.validationGrantAccount(request);

        var validRoles = List.of("counsellor", "supervisor");
        var roleNameMap = Map.of("counsellor", RoleName.ROLE_COUNSELLOR, "supervisor", RoleName.ROLE_SUPERVISOR);

        if (!validRoles.contains(request.getRole())) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Giá trị %s không hợp lệ".formatted(request.getRole()), 10022);
        }

        var role = roleService.findByName(roleNameMap.get(request.getRole()));
        if (role == null) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Giá trị %s không hợp lệ".formatted(request.getRole()), 10023);
        }

        var user = new User(request.getName(),
                request.getEmail().toLowerCase(),
                request.getPhone(), passwordEncoder.encode(request.getPassword()),
                true, role);
        userService.save(user);
        return ResponseEntity.ok(new SuccessResponse("Tạo tài khoản thành công"));
    }

    @PatchMapping("/fields/{id}")
    public ResponseEntity<?> patchStatusField(@PathVariable("id") String id) {
        return handlePatchStatusField(id);
    }

    private ResponseEntity<?> handlePatchStatusField(String id) {
        var field = fieldService.findById(id);
        var status = !field.getStatus();
        field.setStatus(status);
        fieldService.save(field);
        return ResponseEntity.ok(new ApiSuccessResponse<>(status));
    }

    @PostMapping("/fields")
    public ResponseEntity<?> createField(@RequestBody Field request) {
        return handleCreateField(request);
    }

    @PutMapping("/fields/{id}")
    public ResponseEntity<?> updateField(@PathVariable("id") String id, @RequestBody Field request) {
        return handleUpdateField(id, request);
    }

    private ResponseEntity<?> handleCreateField(Field request) {
        validationCreateField(request);
        var fields = new Field(request.getName(), true);
        fieldService.save(fields);
        return ResponseEntity.ok(new SuccessResponse("Thêm lĩnh vực thành công"));
    }

    private ResponseEntity<?> handleUpdateField(String id, Field request) {
        validationUpdateField(id, request);
        var field = fieldService.findById(id);
        field.setName(request.getName());
        fieldService.save(field);
        return ResponseEntity.ok(new SuccessResponse("Cập nhật lĩnh vực thành công"));

    }

    @PostMapping("/departments")
    public ResponseEntity<?> createDepartment(@RequestBody Department request) {
        return handleCreateDepartment(request);
    }


    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") String id, @RequestBody Department request) {
        return handleUpdateDepartment(id, request);
    }

    @PatchMapping("/departments/{id}")
    public ResponseEntity<?> patchStatusDepartment(@PathVariable("id") String id) {
        return handlePatchStatusDepartment(id);
    }

    private ResponseEntity<?> handlePatchStatusDepartment(String id) {
        var department = departmentService.findById(id);
        var status = !department.getStatus();
        department.setStatus(status);
        departmentService.save(department);
        return ResponseEntity.ok(new ApiSuccessResponse<>(status));
    }

    private ResponseEntity<?> handleUpdateDepartment(String id, Department request) {
        validationUpdateDepartment(id, request);
        var department = departmentService.findById(id);
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        departmentService.save(department);
        return ResponseEntity.ok(
                new SuccessResponse("Cập nhật khoa thành công"));
    }

    private ResponseEntity<?> handleCreateDepartment(Department request) {
        validationCreateDepartment(request);
        var department = new Department(request.getName(),
                request.getDescription(), true);
        departmentService.save(department);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse("Thêm khoa thành công"));
    }

    private void validationCreateDepartment(Department request) {
        var name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên khoa không để thể trống", "Tên khoa hiện được nhập đang trống", 10024);
        }
        if (departmentService.existsByName(name)) {
            throw new BadRequestException("Khoa đã tồn tại", "Tên khoa %s đã tồn tại".formatted(name), 10025);
        }
    }

    public void validationUpdateDepartment(String id, Department request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên khoa không để thể trống", "Tên khoa hiện được nhập đang trống", 10026);
        }

        if (departmentService.existsByNameAndIdIsNot(name, id)) {
            throw new BadRequestException("Khoa đã tồn tại", "Tên khoa %s đã tồn tại".formatted(name), 10027);
        }
    }

    private void validationCreateField(Field request) {
        var name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực hiện được nhập đang trống", 10028);
        }
        if (fieldService.existsByName(name)) {
            throw new BadRequestException("Tên lĩnh vực đã tồn tại", "Tên lĩnh vực %s đã tồn tại".formatted(name), 10029);
        }
    }

    public void validationUpdateField(String id, Field request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực hiện được nhập đang trống", 10030);
        }

        if (fieldService.existsByNameAndIdIsNot(name, id)) {
            throw new BadRequestException("Tên lĩnh vực không thể để trống", "Tên lĩnh vực %s đã tồn tại".formatted(name), 10031);
        }
    }
}
