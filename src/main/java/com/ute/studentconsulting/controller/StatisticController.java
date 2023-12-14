package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.payload.request.StatisticRequest;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    private final QuestionService questionService;

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PostMapping("/questions")
    private ResponseEntity<?> handleStatisticQuestions(@RequestBody StatisticRequest request) {
        var date = request.getDate();
        var questions = questionService.findAllByDateBefore(date);
        return ResponseEntity.ok(new ApiSuccessResponse<>(questions));
    }
}
