package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.*;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.model.CounsellorModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.UserPayload;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.*;
import com.ute.studentconsulting.util.AuthUtils;
import com.ute.studentconsulting.util.QuestionUtils;
import com.ute.studentconsulting.util.SortUtils;
import com.ute.studentconsulting.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final AuthUtils authUtils;
    private final SortUtils sortUtils;
    private final UserService userService;
    private final UserUtils userUtils;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final FieldService fieldService;
    private final DepartmentService departmentService;
    private final QuestionService questionService;
    private final QuestionUtils questionUtils;

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(
            @RequestParam(defaultValue = "all", name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort) {
        return handleGetQuestions(value, page, size, sort);
    }

    private ResponseEntity<?> handleGetQuestions(String value, int page, int size, String[] sort) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var departmentHead = authUtils.getCurrentUser();
        Page<Question> questionPage;
        if (value.equals("all")) {
            var ids = departmentHead.getDepartment().getFields().stream().map(Field::getId).toList();
            questionPage = questionService.findByStatusIsAndFieldIdIn(0, ids, pageable);
        } else {
            var field = fieldService.findById(value);
            questionPage = questionService.findByStatusIsAndFieldIs(0, field, pageable);
        }
        var questions = questionUtils.mapQuestionPageToQuestionModels(questionPage);
        var response = new PaginationModel<>(
                questions, questionPage.getNumber(),
                questionPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }


    @DeleteMapping("/users/{userId}/fields/{fieldId}")
    public ResponseEntity<?> deleteFieldOfUser
            (@PathVariable("userId") String userId, @PathVariable("fieldId") String fieldId) {
        return handleDeleteFieldOfUser(userId, fieldId);
    }

    private ResponseEntity<?> handleDeleteFieldOfUser(String userId, String fieldId) {
        var user = userService.findById(userId);
        var ids = getIds(user, fieldId);
        var fields = fieldService.findAllByIdIn(ids);
        var fieldSet = new HashSet<>(fields);
        user.setFields(fieldSet);
        userService.save(user);
        return ResponseEntity.ok(new SuccessResponse(true, "Xóa lĩnh vực của tư vấn viên thành công"));
    }

    @PostMapping("/fields/users/{userId}")
    public ResponseEntity<?> addFieldsToUser
            (@PathVariable("userId") String userId, @RequestBody Map<String, List<String>> fieldIds) {
        return handleAddFieldsToUser(userId, fieldIds);
    }

    private ResponseEntity<?> handleAddFieldsToUser(String userId, Map<String, List<String>> fieldIds) {
        var user = userService.findById(userId);
        return handleAddFieldsToEntity(user, fieldIds);
    }

    private ResponseEntity<?> handleAddFieldsToEntity(Object entity, Map<String, List<String>> fieldIds) {
        if (!(entity instanceof Department) && !(entity instanceof User)) {
            throw new BadRequestException("Lỗi thêm lĩnh vực cho khoa/tư vấn viên",
                    "Đối tượng truyền vào không phải là một thể hiện của Department/User", 10032);
        }
        if (fieldIds == null || !fieldIds.containsKey("ids")) {
            throw new BadRequestException("Danh sách lĩnh vực không hợp lệ", "Danh sách lĩnh vực không tồn tại hoặc không", 10033);
        }
        var ids = fieldIds.get("ids");
        var entityFields = new ArrayList<>(ids);
        if (entity instanceof User user) {
            entityFields.addAll(user.getFields().stream().map(Field::getId).toList());
            var fields = fieldService.findAllByIdInAndStatusIs(entityFields, true);
            var fieldSet = new HashSet<>(fields);
            user.setFields(fieldSet);
            userService.save(user);
        } else {
            Department department = (Department) entity;
            entityFields.addAll(department.getFields().stream().map(Field::getId).toList());
            var fields = fieldService.findAllByIdInAndStatusIs(entityFields, true);
            var fieldSet = new HashSet<>(fields);
            department.setFields(fieldSet);
            departmentService.save(department);
        }
        return ResponseEntity.ok(new SuccessResponse(true, "Thêm lĩnh vực thành công"));
    }

    @GetMapping("/fields/users/{id}")
    public ResponseEntity<?> getFieldsNoneUser(@PathVariable("id") String id) {
        return handleGetFieldsNoneUser(id);
    }

    @GetMapping("/departments/fields")
    public ResponseEntity<?> getFieldsNoneDepartment() {
        return handleGetFieldsNoneDepartment();
    }

    private ResponseEntity<?> handleGetFieldsNoneDepartment() {
        var department = authUtils.getCurrentUser().getDepartment();
        var idsOfDepartment = department.getFields().stream().map(Field::getId).toList();
        var fields = idsOfDepartment.isEmpty() ? fieldService.findAllByStatusIs(true) : fieldService
                .findAllByIdIsNotInAndStatusIs(idsOfDepartment, true);
        return ResponseEntity.ok(new ApiSuccessResponse<>(fields));
    }

    private ResponseEntity<?> handleGetFieldsNoneUser(String id) {
        var department = authUtils.getCurrentUser().getDepartment();
        var counsellor = userService.findByIdAndDepartmentIs(id, department);
        var idsOfDepartment = department.getFields().stream().map(Field::getId).toList();
        var idsOfUser = counsellor.getFields().stream().map(Field::getId).toList();
        var ids = new ArrayList<>(idsOfDepartment);
        if (!idsOfUser.isEmpty()) {
            ids.removeAll(idsOfUser);
        }
        var fields = fieldService.findAllByIdInAndStatusIs(ids, true);
        return ResponseEntity.ok(new ApiSuccessResponse<>(fields));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getCounsellorInMyDepartment(@PathVariable("id") String id) {
        return handleGetCounsellorInMyDepartment(id);
    }

    private ResponseEntity<?> handleGetCounsellorInMyDepartment(String id) {
        var department = authUtils.getCurrentUser().getDepartment();
        var counsellor = userService.findByIdAndDepartmentIs(id, department);
        var ids = counsellor.getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdIn(ids);
        var response = new CounsellorModel(
                counsellor.getId(), counsellor.getName(),
                counsellor.getEmail(), counsellor.getPhone(),
                counsellor.getAvatar(), fields);
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }

    @GetMapping("/fields")
    public ResponseEntity<?> getFieldsInMyDepartment() {
        return handleGetFieldsInMyDepartment();
    }

    private ResponseEntity<?> handleGetFieldsInMyDepartment() {
        var departmentHead = authUtils.getCurrentUser();
        var ids = departmentHead.getDepartment().getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdInAndStatusIs(ids, true);
        return ResponseEntity.ok(new ApiSuccessResponse<>(fields));
    }


    @DeleteMapping("/fields/{id}")
    public ResponseEntity<?> deleteField(@PathVariable("id") String id) {
        return handleDeleteFieldOfDepartment(id);
    }

    private ResponseEntity<?> handleDeleteFieldOfDepartment(String fieldId) {
        var department = authUtils.getCurrentUser().getDepartment();
        var ids = getIds(department, fieldId);
        var fields = fieldService.findAllByIdIn(ids);
        var fieldSet = new HashSet<>(fields);
        department.setFields(fieldSet);
        departmentService.save(department);
        return ResponseEntity.ok(new SuccessResponse(true, "Xóa lĩnh vực phòng ban thành công"));
    }

    private List<String> getIds(Object entity, String fieldId) {
        if (!(entity instanceof Department) && !(entity instanceof User)) {
            throw new BadRequestException("Lỗi thêm lĩnh vực cho khoa/tư vấn viên",
                    "Đối tượng truyền vào không phải là một thể hiện của Department/User", 10034);
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
        return handlePatchAccessibilityUser(id);
    }

    private ResponseEntity<?> handlePatchAccessibilityUser(String id) {
        var departmentHead = authUtils.getCurrentUser();
        var user = userService.findById(id);
        if (!user.getDepartment().equals(departmentHead.getDepartment())
                || !user.getRole().getName().equals(RoleName.ROLE_COUNSELLOR)) {
            throw new BadRequestException("Lỗi vô hiệu hóa tài khoản không hợp lệ",
                    "Người dùng không nằm trong phòng ban hoặc không phải là tư vấn viên", 10035);
        }
        var enabled = !user.getEnabled();
        user.setEnabled(enabled);
        userService.save(user);
        return ResponseEntity.ok(new ApiSuccessResponse<>(enabled));
    }


    @PostMapping("/fields")
    public ResponseEntity<?> addNewFieldToMyDepartment(@RequestBody Map<String, List<String>> fieldIds) {
        return handleAddNewFieldToMyDepartment(fieldIds);
    }

    private ResponseEntity<?> handleAddNewFieldToMyDepartment(Map<String, List<String>> fieldIds) {
        var department = authUtils.getCurrentUser().getDepartment();
        return handleAddFieldsToEntity(department, fieldIds);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserPayload request) {
        return handleCreateUser(request);
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        userUtils.validationGrantAccount(request);
        if (!request.getRole().equals("counsellor")) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Quyền truy cập " + request.getRole() + " không hợp lệ", 10036);
        }
        var role = roleService.findByName(RoleName.ROLE_COUNSELLOR);
        var department = authUtils.getCurrentUser().getDepartment();
        var user = new User(
                request.getName(), request.getEmail().toLowerCase(),
                request.getPhone(), passwordEncoder.encode(request.getPassword()),
                true, role);
        user.setOccupation(request.getOccupation());
        user.setDepartment(department);
        userService.save(user);
        return ResponseEntity.ok(new SuccessResponse(true, "Tạo tài khoản thành công"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsersInDepartment(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "status") String status) {
        return handleGetUsersInDepartment(value, page, size, sort, status);
    }

    private ResponseEntity<?> handleGetUsersInDepartment(String value, int page, int size, String[] sort, String
            status) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var departmentHead = authUtils.getCurrentUser();
        var userPage = switch (status) {
            case "active" -> (value == null)
                    ? userService.findAllByDepartmentIsAndIdIsNotAndEnabledIs
                    (pageable, departmentHead.getDepartment(), departmentHead.getId(), true)
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIs
                    (value, departmentHead.getDepartment(), departmentHead.getId(), true, pageable);
            case "inactive" -> (value == null)
                    ? userService.findAllByDepartmentIsAndIdIsNotAndEnabledIs
                    (pageable, departmentHead.getDepartment(), departmentHead.getId(), false)
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIs
                    (value, departmentHead.getDepartment(), departmentHead.getId(), false, pageable);
            default -> (value == null)
                    ? userService.findAllByDepartmentIsAndIdIsNot(pageable, departmentHead.getDepartment(), departmentHead.getId())
                    : userService.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
                    (value, departmentHead.getDepartment(), departmentHead.getId(), pageable);
        };

        var users = userUtils.mapUserPageToUserModels(userPage);
        var response = new PaginationModel<>(
                users, userPage.getNumber(),
                userPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }

}
