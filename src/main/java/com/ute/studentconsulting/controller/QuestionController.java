package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.model.AnswerModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.model.QuestionDetailsModel;
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
                    var answer = question.getAnswer();
                    var answerModel = new AnswerModel();
                    if (answer != null) {
                        var staff = userService.findById(question.getAnswer().getStaff().getId());
                        answerModel.setId(answer.getId());
                        answerModel.setContent(answerModel.getContent());
                        answerModel.setDate(simpleDateFormat.format(answer.getDate()));
                        answerModel.setUserId(staff.getId());
                        answerModel.setName(staff.getName());
                        answerModel.setEmail(staff.getEmail());
                        answerModel.setAvatar(staff.getAvatar());
                    }
                    return new QuestionDetailsModel(
                            user.getId(),
                            user.getName(),
                            user.getAvatar(),
                            question.getId(), question.getTitle(),
                            question.getContent(), simpleDateFormat.format(question.getDate()),
                            answerModel
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
