package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.RoleName;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.AppException;
import com.ute.studentconsulting.model.CounsellorModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.RoleService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.SortUtility;
import com.ute.studentconsulting.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/department-head")
@RequiredArgsConstructor
@Slf4j
public class DepartmentHeadController {
    private final AuthUtility authUtility;
    private final SortUtility sortUtility;
    private final UserService userService;
    private final UserUtility userUtility;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final FieldService fieldService;
    private final DepartmentService departmentService;

    @PostMapping("/fields/users/{userId}")
    public ResponseEntity<?> addFieldsToUser
            (@PathVariable("userId") String userId, @RequestBody Map<String, List<String>> fieldIds) {
        try {
            return handleAddFieldsToUser(userId, fieldIds);
        } catch (Exception e) {
            log.error("Thêm trưởng khoa thất bại: {}", e.getMessage());
            throw new AppException("Thêm trưởng khoa thất bại: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleAddFieldsToUser(String userId, Map<String, List<String>> fieldIds) {
        var user = userService.findById(userId);
        return handleAddFieldsToEntity(user, fieldIds);
    }

    private ResponseEntity<?> handleAddFieldsToEntity(Object entity, Map<String, List<String>> fieldIds) {
        if (fieldIds == null || !fieldIds.containsKey("ids")) {
            return new ResponseEntity<>(new MessageResponse(false, "Danh sách lĩnh vực không hợp lệ."), HttpStatus.BAD_REQUEST);
        }
        var ids = fieldIds.get("ids");
        var fields = fieldService.findAllByIdIn(ids);
        var fieldSet = new HashSet<>(fields);
        if (entity instanceof User user) {
            user.setFields(fieldSet);
            userService.save(user);
        } else if (entity instanceof Department department) {
            department.setFields(fieldSet);
            departmentService.save(department);
        } else {
            throw new AppException("Thực hiện thêm lĩnh vực cho khoa/tư vấn viên lỗi. Đối tượng truyền vào không phải là một thể hiện của Department/User");
        }
        return new ResponseEntity<>(new MessageResponse(true, "Thêm lĩnh vực thành công."), HttpStatus.OK);
    }

    @GetMapping("/fields/users/{id}")
    public ResponseEntity<?> getFieldsNoneUser(@PathVariable("id") String id) {
        try {
            return handleGetFieldsNoneUser(id);
        } catch (Exception e) {
            log.error("Lỗi lấy vực chưa tồn tại của nhân viên: {}", e.getMessage());
            throw new AppException("Lỗi lấy vực chưa tồn tại của nhân viên: " + e.getMessage());
        }
    }

    @GetMapping("/departments/fields")
    public ResponseEntity<?> getFieldsNoneDepartment() {
        try {
            return handleGetFieldsNoneDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy vực chưa tồn tại của nhân viên: {}", e.getMessage());
            throw new AppException("Lỗi lấy vực chưa tồn tại của nhân viên: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleGetFieldsNoneDepartment() {
        var department = authUtility.getCurrentUser().getDepartment();
        var idsOfDepartment = department.getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdIsNotIn(idsOfDepartment);
        return ResponseEntity.ok(new ApiResponse<>(true, fields));
    }

    private ResponseEntity<?> handleGetFieldsNoneUser(String id) {
        var department = authUtility.getCurrentUser().getDepartment();
        var counsellor = userService.findByIdAndDepartmentIs(id, department);
        var idsOfDepartment = department.getFields().stream().map(Field::getId).toList();
        var idsOfUser = counsellor.getFields().stream().map(Field::getId).toList();
        var ids = new ArrayList<>(idsOfDepartment);
        if (!idsOfUser.isEmpty()) {
            ids.removeAll(idsOfUser);
        }
        var fields = fieldService.findAllByIdIn(ids);
        return ResponseEntity.ok(new ApiResponse<>(true, fields));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getCounsellorInMyDepartment(@PathVariable("id") String id) {
        try {
            return handleGetCounsellorInMyDepartment(id);
        } catch (Exception e) {
            log.error("Lõi xem thông tin nhân viên: {}", e.getMessage());
            throw new AppException("Lõi xem thông tin nhân viên: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleGetCounsellorInMyDepartment(String id) {
        var department = authUtility.getCurrentUser().getDepartment();
        var counsellor = userService.findByIdAndDepartmentIs(id, department);
        var ids = counsellor.getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdIn(ids);
        var response = new CounsellorModel(
                counsellor.getId(),
                counsellor.getName(),
                counsellor.getEmail(),
                counsellor.getPhone(),
                counsellor.getAvatar(),
                fields
        );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @GetMapping("/fields")
    public ResponseEntity<?> getFieldsInMyDepartment() {
        try {
            return handleGetFieldsInMyDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy lĩnh vực: {}", e.getMessage());
            throw new AppException("Lỗi lấy lĩnh vực: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleGetFieldsInMyDepartment() {
        var departmentHead = authUtility.getCurrentUser();
        var ids = departmentHead.getDepartment().getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdIn(ids);
        return ResponseEntity.ok(new ApiResponse<>(true, fields));
    }


    @DeleteMapping("/fields/{id}")
    public ResponseEntity<?> deleteField(@PathVariable("id") String id) {
        try {
            return handleDeleteField(id);
        } catch (Exception e) {
            log.error("Lỗi xóa lĩnh vực khổi khoa: {}", e.getMessage());
            throw new AppException("Lỗi xóa lĩnh vực khổi khoa: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleDeleteField(String id) {
        var department = authUtility.getCurrentUser().getDepartment();
        var ids = department.getFields().stream().map(Field::getId).filter(fid -> !fid.equals(id)).toList();
        var fields = fieldService.findAllByIdIn(ids);
        var fieldSet = new HashSet<>(fields);
        department.setFields(fieldSet);
        departmentService.save(department);
        return ResponseEntity.ok(new MessageResponse(true, "Xóa lĩnh vực thành công."));
    }


    @PatchMapping("/users/{id}")
    public ResponseEntity<?> patchAccessibilityUser(@PathVariable("id") String id) {
        try {
            return handlePatchAccessibilityUser(id);
        } catch (Exception e) {
            log.error("Lỗi khóa/mở khóa tài khoản: {}", e.getMessage());
            throw new AppException("Lỗi khóa/mở khóa tài khoản: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handlePatchAccessibilityUser(String id) {
        var departmentHead = authUtility.getCurrentUser();
        var user = userService.findById(id);
        if (!user.getDepartment().equals(departmentHead.getDepartment())
                || !user.getRole().getName().equals(RoleName.ROLE_COUNSELLOR)) {
            return new ResponseEntity<>(new MessageResponse(false, "Vô hiệu hóa tài khoản không hợp lệ."), HttpStatus.FORBIDDEN);
        }
        var enabled = !user.getEnabled();
        user.setEnabled(enabled);
        userService.save(user);
        return new ResponseEntity<>(
                new ApiResponse<>(true, enabled),
                HttpStatus.OK);
    }


    @PostMapping("/fields")
    public ResponseEntity<?> addNewFieldToMyDepartment(@RequestBody Map<String, List<String>> fieldIds) {
        try {
            return handleAddNewFieldToMyDepartment(fieldIds);
        } catch (Exception e) {
            log.error("Lỗi thêm vĩnh vực vào khoa: {}", e.getMessage());
            throw new AppException("Lỗi thêm vĩnh vực vào khoa: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleAddNewFieldToMyDepartment(Map<String, List<String>> fieldIds) {
        var department = authUtility.getCurrentUser().getDepartment();
        return handleAddFieldsToEntity(department, fieldIds);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserPayload request) {
        try {
            return handleCreateUser(request);
        } catch (Exception e) {
            log.error("Lỗi thêm người dùng: {}", e.getMessage());
            throw new AppException("Lỗi thêm người dùng: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        var error = userUtility.validationGrantAccount(request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }

        if (!request.getRole().equals("counsellor")) {
            return new ResponseEntity<>(new MessageResponse(false, String.format("Quyền truy cập \"%s\" không hợp lệ.", request.getRole())), HttpStatus.BAD_REQUEST);
        }

        var role = roleService.findByName(RoleName.ROLE_COUNSELLOR);
        var department = authUtility.getCurrentUser().getDepartment();
        var user = new User(
                request.getName(),
                request.getEmail().toLowerCase(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                true,
                role);
        user.setOccupation(request.getOccupation());
        user.setDepartment(department);
        userService.save(user);
        return ResponseEntity.ok(new MessageResponse(true, "Tạo tài khoản thành công."));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsersInDepartment(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "status") String status) {
        try {
            return handleGetUsersInDepartment(value, page, size, sort, status);
        } catch (Exception e) {
            log.error("Lỗi lọc, phân trang người dùng trong khoa: {}", e.getMessage());
            throw new AppException("Lỗi lọc, phân trang người dùng trong khoa: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleGetUsersInDepartment(String value, int page, int size, String[] sort, String
            status) {
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var departmentHead = authUtility.getCurrentUser();
        var userPage = switch (status) {
            case "active" -> (value == null)
                    ? userService.findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue
                    (pageable, departmentHead.getDepartment(), departmentHead.getId())
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsTrue
                    (value, departmentHead.getDepartment(), departmentHead.getId(), pageable);
            case "inactive" -> (value == null)
                    ? userService.findAllByDepartmentIsAndIdIsNotAndEnabledIsFalse
                    (pageable, departmentHead.getDepartment(), departmentHead.getId())
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsFalse
                    (value, departmentHead.getDepartment(), departmentHead.getId(), pageable);
            default -> (value == null)
                    ? userService.findAllByDepartmentIsAndIdIsNot(pageable, departmentHead.getDepartment(), departmentHead.getId())
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
                    (value, departmentHead.getDepartment(), departmentHead.getId(), pageable);
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


}
