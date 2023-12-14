package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.model.*;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.service.QuestionService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.util.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    private final FieldService fieldService;
    private final DepartmentService departmentService;
    private final SortUtils sortUtils;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestion(@PathVariable("id") String id) {
        return handleGetQuestion(id);
    }

    private ResponseEntity<?> handleGetQuestion(String id) {
        var question = questionService.findByIdAndStatusIsNot(id, 2);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        var user = question.getUser();
        var answerDetails = getAnswerDetails(question, simpleDateFormat);

        var questionDetails = new QuestionDetailsModel(
                question.getTitle(),
                question.getContent(),
                simpleDateFormat.format(question.getDate()),
                question.getViews(),
                new UserDetailsModel(user.getName(), user.getAvatar()),
                question.getDepartment().getName(),
                answerDetails
        );
        return ResponseEntity.ok(new ApiSuccessResponse<>(questionDetails));
    }

    private AnswerDetailsModel getAnswerDetails(Question question, SimpleDateFormat simpleDateFormat) {
        if (question.getStatus() == 1) {
            var staff = userService.findById(question.getAnswer().getStaff().getId());
            return new AnswerDetailsModel(
                    question.getAnswer().getContent(),
                    simpleDateFormat.format(question.getAnswer().getDate()),
                    new UserDetailsModel(staff.getName(), staff.getAvatar())
            );
        }
        return null;
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchViewsQuestion(@PathVariable("id") String id) {
        return handlePatchViewsQuestion(id);
    }

    private ResponseEntity<?> handlePatchViewsQuestion(String id) {
        // status <> 2 (private)
        var question = questionService.findByIdAndStatusIsNot(id, 2);
        var views = question.getViews() + 1;
        question.setViews(views);
        questionService.save(question);
        return ResponseEntity.ok(new SuccessResponse("Tăng lượt xem bài viết thành công"));
    }

    @GetMapping
    private ResponseEntity<?> getQuestions(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "departmentId") String departmentId,
            @RequestParam(defaultValue = "all", name = "fieldId") String fieldId) {
        return handleGetQuestions(value, page, size, sort, departmentId, fieldId);
    }

    // value = null, department = all/value, field = all
    private Page<Question> getQuestionByDepartmentIsAndFieldIsAll
    (String departmentId, Pageable pageable) {
        return departmentId.equals("all")
                // 2 = private
                // value = null, department = all, field = all
                ? questionService.findAllByStatusIsNot(2, pageable)
                // value = null, department = value, field = all
                : questionService.findAllByDepartmentIsAndStatusIsNot
                (departmentService.findByIdAndStatusIs(departmentId, true), 2, pageable);
    }

    // value = null, department = all/value, field = value
    private Page<Question> getQuestionByDepartmentIsAndFieldIs(String departmentId, String fieldId, Pageable pageable) {
        var field = fieldService.findById(fieldId);
        return departmentId.equals("all")
                // value = null, department = value, field = value
                ? questionService.findAllByFieldIsAndStatusIsNot(field, 2, pageable)
                // value = null, department = value, field = value
                : questionService.findAllByFieldIsAndDepartmentIsAndStatusIsNot
                (field, departmentService.findByIdAndStatusIs(departmentId, true), 2, pageable);
    }

    // value = value, department = all/value, field = all
    private Page<Question> getQuestionByDepartmentIsAndFieldIsAllAndSearch
    (String value, String departmentId, Pageable pageable) {
        return departmentId.equals("all")
                // value = value, department = all, field = all
                ? questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatusIsNot
                (value, 2, pageable)
                // value = value, department = value, field = all
                : questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndStatusIsNot
                (value, departmentService.findById(departmentId), 2, pageable);
    }

    // value = value, department = all/value, field = all
    private Page<Question> getQuestionByDepartmentIsAllAndFieldIsAndSearch
    (String value, String departmentId, String fieldId, Pageable pageable) {
        var field = fieldService.findById(fieldId);
        return departmentId.equals("all")
                ? questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndStatusIsNot(value, field, 2, pageable)
                : questionService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIsAndStatusIsNot
                (value, departmentService.findByIdAndStatusIs(departmentId, true), field, 2, pageable);
    }


    private ResponseEntity<?> handleGetQuestions(String value, int page, int size, String[] sort, String departmentId, String fieldId) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var questionPage = (value == null)
                ? getQuestionsPage(departmentId, fieldId, pageable)
                : getQuestionsPageAndSearch(value, departmentId, fieldId, pageable);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var allQuestionInfo = questionPage.getContent().stream()
                .map(question -> {

                    var user = question.getUser();
                    var department = question.getDepartment();

                    return new QuestionHomeModel(
                            user.getId(),
                            user.getName(),
                            user.getAvatar(),
                            department.getName(),
                            question.getId(), question.getTitle(),
                            question.getStatus(),
                            simpleDateFormat.format(question.getDate())
                    );
                }).toList();
        var response = new PaginationModel<>(
                allQuestionInfo, questionPage.getNumber(),
                questionPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }

    private Page<Question> getQuestionsPage
            (String departmentId, String fieldId, Pageable pageable) {
        return fieldId.equals("all")
                ? getQuestionByDepartmentIsAndFieldIsAll(departmentId, pageable)
                : getQuestionByDepartmentIsAndFieldIs(departmentId, fieldId, pageable);
    }


    private Page<Question> getQuestionsPageAndSearch
            (String value, String departmentId, String fieldId, Pageable pageable) {
        return fieldId.equals("all")
                ? getQuestionByDepartmentIsAndFieldIsAllAndSearch(value, departmentId, pageable)
                : getQuestionByDepartmentIsAllAndFieldIsAndSearch(value, departmentId, fieldId, pageable);
    }
}
