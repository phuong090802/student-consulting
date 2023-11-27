package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ServerException;
import com.ute.studentconsulting.payloads.QuestionPayload;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.QuestionService;
import com.ute.studentconsulting.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            throw new ServerException("Lỗi đặt câu hỏi", e.getMessage(), 10085);
        }
    }

    private ResponseEntity<?> handleCreateQuestion(QuestionPayload request) {
        validationQuestion(request);
        var user = authUtility.getCurrentUser();
        var department = departmentService.findByIdAndStatusIsTrue(request.getDepartmentId());
        var field = fieldService.findById(request.getFieldId());
        var question = new Question(
                request.getTitle(),
                request.getContent(),
                new Date(),
                0,
                0,
                user,
                department,
                field);

        questionService.save(question);

        return ResponseEntity.ok(
                new MessageResponse(true, "Đặt câu hỏi thành công"));
    }

    private void validationQuestion(QuestionPayload request) {
        var title = request.getTitle().trim();
        if (title.isEmpty()) {
            throw new BadRequestException("Tiêu đề không thể để trống", "Tiêu đề được nhập trống", 10086);
        }

        var content = request.getContent().trim();
        if (content.isEmpty()) {
            throw new BadRequestException("Nội dung không thể để trống", "Nội dung được nhập trống", 10087);
        }

        var departmentId = request.getDepartmentId().trim();
        if (departmentId.isEmpty()) {
            throw new BadRequestException("Khoa không thể để trống", "Khoa được nhập trống", 10088);
        }

        var fieldId = request.getFieldId().trim();
        if (fieldId.isEmpty()) {
            throw new BadRequestException("Lĩnh vực không thể để trống", "Lĩnh vực được nhập trống", 10089);
        }
    }
}
