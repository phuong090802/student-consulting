package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.payload.QuestionPayload;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.QuestionService;
import com.ute.studentconsulting.util.AuthUtils;
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
    private final AuthUtils authUtils;
    private final DepartmentService departmentService;
    private final FieldService fieldService;
    private final QuestionService questionService;

    @PostMapping("/questions")
    private ResponseEntity<?> createQuestion(@RequestBody QuestionPayload request) {
        return handleCreateQuestion(request);
    }

    private ResponseEntity<?> handleCreateQuestion(QuestionPayload request) {
        validationQuestion(request);
        var user = authUtils.getCurrentUser();
        var department = departmentService.findByIdAndStatusIs(request.getDepartmentId(), true);
        var field = fieldService.findById(request.getFieldId());
        var question = new Question(request.getTitle(), request.getContent(),
                new Date(), 0, 0, user, department, field);
        questionService.save(question);
        return ResponseEntity.ok(new SuccessResponse(true, "Đặt câu hỏi thành công"));
    }

    private void validationQuestion(QuestionPayload request) {
        var title = request.getTitle().trim();
        if (title.isEmpty()) {
            throw new BadRequestException("Tiêu đề không thể để trống", "Tiêu đề bị trống", 10038);
        }

        var content = request.getContent().trim();
        if (content.isEmpty()) {
            throw new BadRequestException("Nội dung không thể để trống", "Nội dung bị trống", 10039);
        }

        var departmentId = request.getDepartmentId().trim();
        if (departmentId.isEmpty()) {
            throw new BadRequestException("Khoa không thể để trống", "Khoa bị trống", 10040);
        }

        var fieldId = request.getFieldId().trim();
        if (fieldId.isEmpty()) {
            throw new BadRequestException("Lĩnh vực không thể để trống", "Lĩnh vực bị trống", 10041);
        }
    }
}
