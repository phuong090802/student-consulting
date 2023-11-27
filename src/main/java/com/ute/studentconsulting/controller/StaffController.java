package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.ForwardQuestion;
import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.exception.BadRequestException;
import com.ute.studentconsulting.exception.ServerException;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.model.QuestionModel;
import com.ute.studentconsulting.payloads.request.AnswerRequest;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.*;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.QuestionUtility;
import com.ute.studentconsulting.utility.SortUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
public class StaffController {
    private final AuthUtility authUtility;
    private final FieldService fieldService;
    private final SortUtility sortUtility;
    private final QuestionService questionService;
    private final QuestionUtility questionUtility;
    private final AnswerService answerService;
    private final DepartmentService departmentService;
    private final ForwardQuestionService forwardQuestionService;

    @PatchMapping("/questions/{questionId}/departments/{departmentId}")
    public ResponseEntity<?> forwardQuestion(
            @PathVariable("questionId") String questionId,
            @PathVariable("departmentId") String departmentId
    ) {
        try {
            return handleForwardQuestion(questionId, departmentId);
        } catch (Exception e) {
            log.error("Chuyển tiếp câu hỏi bị lỗi: {}", e.getMessage());
            throw new ServerException("Chuyển tiếp câu hỏi bị lỗi", e.getMessage(), 10078);
        }
    }

    private ResponseEntity<?> handleForwardQuestion(String questionId, String departmentId) {
        var question = questionService.findById(questionId);
        var fromDepartment = question.getDepartment();
        var toDepartment = departmentService.findById(departmentId);
        var ids = toDepartment.getFields().stream().map(Field::getId).toList();
        if (!ids.contains(question.getField().getId())) {
            throw new BadRequestException("Phòng ban mới không hỗ trợ lĩnh vực này", "Phòng ban nhận câu hỏi chuyển tiếp không hỗ trợ lĩnh vực này", 10079);
        }
        question.setDepartment(toDepartment);
        questionService.save(question);
        forwardQuestionService.save(new ForwardQuestion(fromDepartment, toDepartment, question));
        return ResponseEntity.ok(
                new MessageResponse(true, "Chuyển tiếp câu hỏi thành công"));
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable("id") String id) {
        try {
            return handleGetQuestionById(id);
        } catch (Exception e) {
            log.error("Lỗi lấy câu hỏi bằng id: {}", e.getMessage());
            throw new ServerException("Lỗi lấy câu hỏi bằng id", e.getMessage(), 10080);
        }
    }

    private ResponseEntity<?> handleGetQuestionById(String id) {
        var question = questionService.findById(id);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var response = new QuestionModel(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                simpleDateFormat.format(question.getDate()),
                question.getUser().getId(),
                question.getUser().getName(),
                question.getUser().getEmail(),
                question.getDepartment().getName(),
                question.getField().getName());
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @PostMapping("/answers")
    public ResponseEntity<?> answerQuestion(@RequestBody AnswerRequest request) {
        try {
            return handleAnswerQuestion(request);
        } catch (Exception e) {
            log.error("Lỗi trả lời câu hỏi: {}", e.getMessage());
            throw new ServerException("Lỗi trả lời câu hỏi", e.getMessage(), 10081);
        }
    }

    private ResponseEntity<?> handleAnswerQuestion(AnswerRequest request) {
        var staff = authUtility.getCurrentUser();
        var question = questionService.findById(request.getQuestionId());
        var answer = new Answer(request.getContent(), new Date(), false, staff, question);
        answerService.save(answer);
        question.setStatus(1);
        questionService.save(question);
        return new ResponseEntity<>(
                new MessageResponse(true, "Trả lời câu hỏi thành công"),
                HttpStatus.CREATED);
    }

    @GetMapping("/questions/had-questions")
    public ResponseEntity<?> getCheckHadQuestion(
            @RequestParam(defaultValue = "all", name = "value") String value
    ) {
        try {
            return handleGetCheckHadQuestion(value);
        } catch (Exception e) {
            log.error("Lỗi tư vấn viên kiểm tra có câu hỏi chưa trả lời: {}", e.getMessage());
            throw new ServerException("Lỗi tư vấn viên kiểm tra có câu hỏi chưa trả lời", e.getMessage(), 10082);
        }
    }

    private ResponseEntity<?> handleGetCheckHadQuestion(String value) {
        boolean hadQuestions;
        if (value.equals("all")) {
            var staff = authUtility.getCurrentUser();
            var ids = staff.getFields().stream().map(Field::getId).toList();
            hadQuestions = questionService.existsByStatusIsAndFieldIdIn(0, ids);
        } else {
            var field = fieldService.findById(value);
            hadQuestions = questionService.existsByStatusIsAndFieldIs(0, field);
        }
        return ResponseEntity.ok(new ApiResponse<>(true, hadQuestions));
    }

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
            throw new ServerException("Lỗi lọc, phân trang câu hỏi", e.getMessage(), 10083);
        }
    }

    private ResponseEntity<?> handleGetQuestions(String value, int page, int size, String[] sort) {
        var staff = authUtility.getCurrentUser();
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Question> questionPage;
        if (value.equals("all")) {
            var ids = staff.getFields().stream().map(Field::getId).toList();
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

    @GetMapping("/fields")
    public ResponseEntity<?> getFieldsInMyDepartment() {
        try {
            return handleGetFieldsInMyDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy lĩnh vực của ban thân: {}", e.getMessage());
            throw new ServerException("Lỗi lấy lĩnh vực của ban thân", e.getMessage(), 10084);
        }
    }

    private ResponseEntity<?> handleGetFieldsInMyDepartment() {
        var staff = authUtility.getCurrentUser();
        var ids = staff.getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdIn(ids);
        return ResponseEntity.ok(new ApiResponse<>(true, fields));
    }
}
