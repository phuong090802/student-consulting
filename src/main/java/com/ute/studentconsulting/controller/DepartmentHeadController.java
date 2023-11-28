package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.*;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.model.AnswerModel;
import com.ute.studentconsulting.model.CounsellorModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.FAQPayload;
import com.ute.studentconsulting.payload.UserPayload;
import com.ute.studentconsulting.payload.request.FieldsRequest;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.*;
import com.ute.studentconsulting.util.*;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private final FAQService faqService;
    private final AnswerService answerService;
    private final FAQUtils faqUtils;

    @GetMapping("/faqs")
    private ResponseEntity<?> getAllFAQ(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "title, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "fieldId") String fieldId) {
        return handleGetAllFAQ(value, page, size, sort, fieldId);
    }

    private ResponseEntity<?> handleGetAllFAQ
            (String value, int page, int size, String[] sort, String fieldId) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var user = authUtils.getCurrentUser();
        var department = user.getDepartment();
        var faqPage = (value == null)
                ? getFAQPageAndFieldIs(department, fieldId, pageable)
                : getFAQPageAndFieldIsAndSearch(value, department, fieldId, pageable);
        return faqUtils.getResponseFAQ(faqPage);
    }

    // search = null, department, field = all/value,
    private Page<FAQ> getFAQPageAndFieldIs(Department department, String fieldId, Pageable pageable) {
        // search = null, department, field = all,
        return (fieldId.equals("all")) ? faqService.findAllByDepartmentIs(department, pageable)
                // search = null, department, field = value
                : faqService.findAllByFieldIsAndDepartmentIs(fieldService.findById(fieldId), department, pageable);
    }

    // search <> null, department, field = all/value,
    private Page<FAQ> getFAQPageAndFieldIsAndSearch(String value, Department department, String fieldId, Pageable pageable) {
        // search <> null, department, field = all,
        return (fieldId.equals("all")) ? faqService
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs(value, department, pageable)
                // search <> null, department, field = value,
                : faqService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndDepartmentIs
                (value, fieldService.findById(fieldId), department, pageable);
    }


    @GetMapping("/answers")
    private ResponseEntity<?> getAnswers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort) {
        return handleGetAnswers(page, size, sort);
    }

    private ResponseEntity<?> handleGetAnswers(int page, int size, String[] sort) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var answerPage = answerService.findAllByApprovedIs(false, pageable);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var answers = answerPage.map(answer -> new AnswerModel(
                answer.getId(), answer.getContent(), simpleDateFormat.format(answer.getDate()),
                answer.getStaff().getId(), answer.getStaff().getName(), answer.getStaff().getEmail(), answer.getStaff().getAvatar()
        )).toList();
        var response = new PaginationModel<>(
                answers, answerPage.getNumber(),
                answerPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }

    @PatchMapping("/answers/{id}")
    public ResponseEntity<?> updateApproveAnswer(@PathVariable("id") String id) {
        return handleUpdateApproveAnswer(id);
    }

    private ResponseEntity<?> handleUpdateApproveAnswer(String id) {
        var answer = answerService.findById(id);
        answer.setApproved(true);
        answerService.save(answer);
        return ResponseEntity.ok(new SuccessResponse("Duyệt câu trả lời thành công"));
    }

    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<?> deleteFAQ(@PathVariable("id") String id) {
        faqService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse("Xóa câu hỏi chung thành công"));
    }

    @PutMapping("/faqs/{id}")
    public ResponseEntity<?> updateFAQ(@PathVariable("id") String id, @RequestBody FAQPayload request) {
        return handleUpdateFAQ(id, request);
    }

    private ResponseEntity<?> handleUpdateFAQ(String id, FAQPayload request) {
        var user = authUtils.getCurrentUser();
        validationFAQ(request, user);
        var field = fieldService.findById(request.getFieldId());
        var faq = faqService.findById(id);
        faq.setContent(request.getContent());
        faq.setTitle(request.getTitle());
        faq.setField(field);
        faqService.save(faq);
        return ResponseEntity.ok(new SuccessResponse("Cập nhật câu hỏi chung thành công"));
    }


    @PostMapping("/faqs")
    public ResponseEntity<?> addFAQ(@RequestBody FAQPayload request) {
        return handleAddFAQ(request);
    }

    private ResponseEntity<?> handleAddFAQ(FAQPayload request) {
        var user = authUtils.getCurrentUser();
        validationFAQ(request, user);

        var field = fieldService.findById(request.getFieldId());
        var faq = new FAQ(request.getTitle(), request.getContent(), field, user.getDepartment());
        faqService.save(faq);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SuccessResponse("Tạo câu hỏi chung thành công"));
    }

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
            (@PathVariable("userId") String userId, @RequestBody FieldsRequest request) {
        return handleAddFieldsToUser(userId, request);
    }

    private ResponseEntity<?> handleAddFieldsToUser(String userId, FieldsRequest request) {
        var user = userService.findById(userId);
        return handleAddFieldsToEntity(user, request);
    }

    private ResponseEntity<?> handleAddFieldsToEntity(Object entity, FieldsRequest request) {
        if (!(entity instanceof Department) && !(entity instanceof User)) {
            throw new BadRequestException("Lỗi thêm lĩnh vực cho khoa/tư vấn viên",
                    "Đối tượng truyền vào không phải là một thể hiện của Department/User", 10032);
        }

        var ids = request.getIds();
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
    public ResponseEntity<?> addNewFieldToMyDepartment(@RequestBody FieldsRequest request) {
        return handleAddNewFieldToMyDepartment(request);
    }

    private ResponseEntity<?> handleAddNewFieldToMyDepartment(FieldsRequest request) {
        var department = authUtils.getCurrentUser().getDepartment();
        return handleAddFieldsToEntity(department, request);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserPayload request) {
        return handleCreateUser(request);
    }

    private ResponseEntity<?> handleCreateUser(UserPayload request) {
        userUtils.validationGrantAccount(request);
        if (!request.getRole().equals("counsellor")) {
            throw new BadRequestException("Quyền truy cập không hợp lệ", "Quyền truy cập %s không hợp lệ".formatted(request.getRole()), 10036);
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

    private void validationFAQ(FAQPayload request, User user) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BadRequestException("Tiêu đề câu hỏi chung không thể trống", "Tiêu đề bị trống", 10075);
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new BadRequestException("Nội dung câu hỏi chung không thể trống", "Nội dung bị trống", 10076);
        }
        if (!StringUtils.hasText(request.getFieldId())) {
            throw new BadRequestException("Mã lĩnh vực câu hỏi chung không thể trống", "Mã lĩnh vự bị trống", 10077);
        }
        var ids = user.getDepartment().getFields().stream().map(Field::getId).toList();
        if (!ids.contains(request.getFieldId())) {
            throw new BadRequestException("Lĩnh vực không thuộc phòng ban",
                    "Lĩnh vực với mã: %s không thuộc phòng ban".formatted(request.getFieldId()), 10078);
        }
    }

}
