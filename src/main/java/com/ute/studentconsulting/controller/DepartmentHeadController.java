package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.*;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ServerException;
import com.ute.studentconsulting.model.CounsellorModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payloads.UserPayload;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.*;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.QuestionUtility;
import com.ute.studentconsulting.utility.SortUtility;
import com.ute.studentconsulting.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final QuestionService questionService;
    private final QuestionUtility questionUtility;

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(
            @RequestParam(defaultValue = "all", name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort
    ) {

        try {
            return handleGetQuestions(value, page, size, sort);
        } catch (Exception e) {
            log.error("Lỗi lọc, phân trang câu hỏi: {}", e.getMessage());
            throw new ServerException("Lỗi lọc, phân trang câu hỏi", e.getMessage(), 10057);
        }
    }

    private ResponseEntity<?> handleGetQuestions(String value, int page, int size, String[] sort) {
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var departmentHead = authUtility.getCurrentUser();
        Page<Question> questionPage;
        if (value.equals("all")) {
            var ids = departmentHead.getDepartment().getFields().stream().map(Field::getId).toList();
            questionPage = questionService.findByStatusIsAndFieldIdIn(0, ids, pageable);
        } else {
            var field = fieldService.findById(value);
            questionPage = questionService.findByStatusIsAndFieldIs(0, field, pageable);
        }
        var questions = questionUtility.mapQuestionPageToQuestionModels(questionPage);
        var response =
                new PaginationModel<>(
                        questions,
                        questionPage.getNumber(),
                        questionPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


    @DeleteMapping("/users/{userId}/fields/{fieldId}")
    public ResponseEntity<?> deleteFieldOfUser(@PathVariable("userId") String userId, @PathVariable("fieldId") String fieldId) {
        try {
            return handleDeleteFieldOfUser(userId, fieldId);
        } catch (Exception e) {
            log.error("Lỗi xóa lĩnh vực của người dùng: {}", e.getMessage());
            throw new ServerException("Lỗi xóa lĩnh vực của người dùng", e.getMessage(), 10058);
        }
    }

    private ResponseEntity<?> handleDeleteFieldOfUser(String userId, String fieldId) {
        var user = userService.findById(userId);
        var ids = getIds(user, fieldId);
        var fields = fieldService.findAllByIdIn(ids);
        var fieldSet = new HashSet<>(fields);
        user.setFields(fieldSet);
        userService.save(user);
        return ResponseEntity.ok(new MessageResponse(true, "Xóa lĩnh vực cua tư vấn viên thành công"));
    }

    @PostMapping("/fields/users/{userId}")
    public ResponseEntity<?> addFieldsToUser
            (@PathVariable("userId") String userId, @RequestBody Map<String, List<String>> fieldIds) {
        try {
            return handleAddFieldsToUser(userId, fieldIds);
        } catch (Exception e) {
            log.error("Lỗi lĩnh vực cho tư vấn viên: {}", e.getMessage());
            throw new ServerException("Lỗi lĩnh vực cho tư vấn viên", e.getMessage(), 10059);
        }
    }

    private ResponseEntity<?> handleAddFieldsToUser(String userId, Map<String, List<String>> fieldIds) {
        var user = userService.findById(userId);
        return handleAddFieldsToEntity(user, fieldIds);
    }

    private ResponseEntity<?> handleAddFieldsToEntity(Object entity, Map<String, List<String>> fieldIds) {
        if (!(entity instanceof Department) && !(entity instanceof User)) {
            throw new BadRequestException("Lỗi thêm lĩnh vực cho khoa/tư vấn viên",
                    "Đối tượng truyền vào không phải là một thể hiện của Department/User", 10060);
        }
        if (fieldIds == null || !fieldIds.containsKey("ids")) {
            throw new BadRequestException("Danh sách lĩnh vực không hợp lệ", "Danh sách lĩnh vực không tồn tại hoặc không", 10061);
        }
        var ids = fieldIds.get("ids");
        var entityFields = new ArrayList<>(ids);
        if (entity instanceof User user) {
            entityFields.addAll(user.getFields().stream().map(Field::getId).toList());
            var fields = fieldService.findAllByIdIn(entityFields);
            var fieldSet = new HashSet<>(fields);
            user.setFields(fieldSet);
            userService.save(user);
        } else {
            Department department = (Department) entity;
            entityFields.addAll(department.getFields().stream().map(Field::getId).toList());
            var fields = fieldService.findAllByIdIn(entityFields);
            var fieldSet = new HashSet<>(fields);
            department.setFields(fieldSet);
            departmentService.save(department);
        }
        return new ResponseEntity<>(new MessageResponse(true, "Thêm lĩnh vực thành công"), HttpStatus.OK);
    }

    @GetMapping("/fields/users/{id}")
    public ResponseEntity<?> getFieldsNoneUser(@PathVariable("id") String id) {
        try {
            return handleGetFieldsNoneUser(id);
        } catch (Exception e) {
            log.error("Lỗi lấy vực chưa tồn tại của nhân viên: {}", e.getMessage());
            throw new ServerException("Lỗi lấy vực chưa tồn tại của nhân viên", e.getMessage(), 10062);
        }
    }

    @GetMapping("/departments/fields")
    public ResponseEntity<?> getFieldsNoneDepartment() {
        try {
            return handleGetFieldsNoneDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy vực chưa tồn tại của phòng ban: {}", e.getMessage());
            throw new ServerException("Lỗi lấy vực chưa tồn tại của phòng ban", e.getMessage(), 10063);
        }
    }

    private ResponseEntity<?> handleGetFieldsNoneDepartment() {
        var department = authUtility.getCurrentUser().getDepartment();
        var idsOfDepartment = department.getFields().stream().map(Field::getId).toList();
        var fields = idsOfDepartment.isEmpty() ? fieldService.findAll() : fieldService.findAllByIdIsNotIn(idsOfDepartment);
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
            log.error("Lỗi xem thông tin nhân viên: {}", e.getMessage());
            throw new ServerException("Lỗi xem thông tin nhân viên", e.getMessage(), 10064);
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
            throw new ServerException("Lỗi lấy lĩnh vực", e.getMessage(), 10065);
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
            return handleDeleteFieldOfDepartment(id);
        } catch (Exception e) {
            log.error("Lỗi xóa lĩnh vực khỏi khoa: {}", e.getMessage());
            throw new ServerException("Lỗi xóa lĩnh vực khỏi khoa", e.getMessage(), 10066);
        }
    }

    private ResponseEntity<?> handleDeleteFieldOfDepartment(String fieldId) {
        var department = authUtility.getCurrentUser().getDepartment();
        var ids = getIds(department, fieldId);
        var fields = fieldService.findAllByIdIn(ids);
        var fieldSet = new HashSet<>(fields);
        department.setFields(fieldSet);
        departmentService.save(department);
        return ResponseEntity.ok(new MessageResponse(true, "Xóa lĩnh vực phòng ban thành công"));
    }

    private List<String> getIds(Object entity, String fieldId) {
        if (!(entity instanceof Department) && !(entity instanceof User)) {
            throw new BadRequestException("Lỗi thêm lĩnh vực cho khoa/tư vấn viên",
                    "Đối tượng truyền vào không phải là một thể hiện của Department/User", 10060);
        }
        if (entity instanceof User user) {
            return user.getFields().stream().map(Field::getId).filter(fid -> !fid.equals(fieldId)).toList();
        } else {
            Department department = (Department) entity;
            return department.getFields().stream().map(Field::getId).filter(fid -> !fid.equals(fieldId)).toList();
        }
    }


    @PatchMapping("/users/{id}")
    public ResponseEntity<?> patchAccessibilityUser(@PathVariable("id") String id) {
        try {
            return handlePatchAccessibilityUser(id);
        } catch (Exception e) {
            log.error("Lỗi khóa/mở khóa tài khoản: {}", e.getMessage());
            throw new ServerException("Lỗi khóa/mở khóa tài khoản", e.getMessage(), 10067);
        }
    }

    private ResponseEntity<?> handlePatchAccessibilityUser(String id) {
        var departmentHead = authUtility.getCurrentUser();
        var user = userService.findById(id);
        if (!user.getDepartment().equals(departmentHead.getDepartment())
                || !user.getRole().getName().equals(RoleName.ROLE_COUNSELLOR)) {
            throw new BadRequestException("Lỗi vô hiệu hóa tài khoản không hợp lệ",
                    "Người dùng không nằm trong phòng ban hoặc không phải là tư vấn viên", 10068);
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
            throw new ServerException("Lỗi thêm vĩnh vực vào khoa", e.getMessage(), 10069);
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
            throw new ServerException("Lỗi thêm người dùng", e.getMessage(), 10070);
        }
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        userUtility.validationGrantAccount(request);

        if (!request.getRole().equals("counsellor")) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Quyền truy cập " + request.getRole() + " không hợp lệ", 10071);
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
        return ResponseEntity.ok(new MessageResponse(true, "Tạo tài khoản thành công"));
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
            log.error("Lỗi sắp xếp, tìm kiếm, lọc, phân trang người dùng trong khoa: {}", e.getMessage());
            throw new ServerException("Lỗi sắp xếp, tìm kiếm, lọc, phân trang người dùng trong khoa", e.getMessage(), 10072);

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
