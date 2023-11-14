package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.model.QuestionDetailsModel;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.QuestionService;
import com.ute.studentconsulting.utility.SortUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    private final FieldService fieldService;
    private final DepartmentService departmentService;
    private final SortUtility sortUtility;

    @GetMapping
    private ResponseEntity<?> getQuestions(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "departmentId") String departmentId,
            @RequestParam(defaultValue = "all", name = "fieldId") String fieldId
    ) {
        try {
            return handleGetQuestions(value, page, size, sort, departmentId, fieldId);
        } catch (Exception e) {
            log.error("Lỗi sắp xếp, lọc, phân trang ,tìm kiếm câu hỏi: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi sắp xếp, lọc, phân trang ,tìm kiếm câu hỏi"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // department = value, field = all
    private Page<Question> getQuestionByDepartmentIsAndFieldIsAll
    (String departmentId, String fieldId, Pageable pageable) {
        var field = fieldService.findById(fieldId);
        return departmentId.equals("all")
                ? questionService.findAllByFieldIs(field, pageable)
                : questionService.findAllByDepartmentIsAndFieldIs(departmentService.findByIdAndStatusIsTrue(departmentId), field, pageable);
    }

    // field = all
    private Page<Question> getQuestionByDepartmentIsAllAndFieldIsAll
    (String fieldId, Pageable pageable) {
        return fieldId.equals("all")
                ? questionService.findAll(pageable)
                : questionService.findAllByFieldIs(fieldService.findById(fieldId), pageable);
    }

    // search = value, department = value, field = all
    private Page<Question> getQuestionByDepartmentIsAndFieldIsAllAndSearch
    (String value, String departmentId, String fieldId, Pageable pageable) {
        var department = departmentService.findByIdAndStatusIsTrue(departmentId);
        return fieldId.equals("all")
                ? questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs(value, department, pageable)
                : questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIs
                (value, department, fieldService.findById(fieldId), pageable);
    }

    // search = value, department = all, field = all
    private Page<Question> getQuestionByDepartmentIsAllAndFieldIsAllAndSearch
    (String value, String fieldId, Pageable pageable) {
        return fieldId.equals("all")
                ? questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(value, pageable)
                : questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
                (value, fieldService.findById(fieldId), pageable);
    }


    private ResponseEntity<?> handleGetQuestions(String value, int page, int size, String[] sort, String departmentId, String fieldId) {
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var questionPage = (value == null)
                ? getQuestionsPage(departmentId, fieldId, pageable)
                : getQuestionsPageAndSearch(value, departmentId, fieldId, pageable);

        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var allQuestionInfo = questionPage.getContent().stream()
                .map(question -> new QuestionDetailsModel(
                        question.getUser().getId(),
                        question.getUser().getName(),
                        question.getUser().getAvatar(),
                        question.getId(),
                        question.getTitle(),
                        question.getContent(),
                        simpleDateFormat.format(question.getDate()),
                        List.of()
                )).toList();
        var response =
                new PaginationModel<>(
                        allQuestionInfo,
                        questionPage.getNumber(),
                        questionPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    // status = disabled, role = all
    private Page<Question> getQuestionsPage
    (String departmentId, String fieldId, Pageable pageable) {
        return departmentId.equals("all")
                ? getQuestionByDepartmentIsAllAndFieldIsAll(fieldId, pageable)
                : getQuestionByDepartmentIsAndFieldIsAll(departmentId, fieldId, pageable);
    }


    private Page<Question> getQuestionsPageAndSearch
            (String value, String departmentId, String fieldId, Pageable pageable) {
        return departmentId.equals("all")
                ? getQuestionByDepartmentIsAllAndFieldIsAllAndSearch(value, fieldId, pageable)
                : getQuestionByDepartmentIsAndFieldIsAllAndSearch(value, departmentId, fieldId, pageable);
    }
}
