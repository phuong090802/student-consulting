package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.model.ErrorModel;
import com.ute.studentconsulting.payloads.QuestionPayload;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.QuestionService;
import com.ute.studentconsulting.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final AuthUtility authUtility;
    private final DepartmentService departmentService;
    private final FieldService fieldService;
    private final QuestionService questionService;


    @PostMapping("/questions")
    private ResponseEntity<?> createQuestion(@RequestBody QuestionPayload request) {
        try {
            return handleCreateQuestion(request);
        } catch (Exception e) {
            log.error("Lỗi đặt câu hỏi: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi đặt câu hỏi"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleCreateQuestion(QuestionPayload request) {
        var error = validationQuestion(request);
        if (error != null) {
            return new ResponseEntity<>(new MessageResponse(false, error.getMessage()), error.getStatus());
        }

        var user = authUtility.getCurrentUser();
        var department = departmentService.findByIdAndStatusIsTrue(request.getDepartmentId());
        var field = fieldService.findById(request.getFieldId());
        var question = new Question(
                request.getTitle(),
                request.getContent(),
                new Date(),
                false,
                0,
                user,
                department,
                field);

        questionService.save(question);

        return ResponseEntity.ok(
                new MessageResponse(true, "Đặt câu hỏi thành công"));
    }

    private ErrorModel validationQuestion(QuestionPayload request) {
        var title = request.getTitle().trim();
        if (title.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Tiêu đề không thể để trống");
        }

        var content = request.getContent().trim();
        if (content.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Nội dung không thể để trống");
        }

        var departmentId = request.getDepartmentId().trim();
        if (departmentId.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Khoa không thể để trống");
        }

        var fieldId = request.getFieldId().trim();
        if (fieldId.isEmpty()) {
            return new ErrorModel(HttpStatus.BAD_REQUEST, "Lĩnh vực không thể để trống");
        }
        return null;
    }
}
