package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.model.FeedbackModel;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.ErrorResponse;
import com.ute.studentconsulting.payload.response.SuccessResponse;
import com.ute.studentconsulting.service.FeedbackService;
import com.ute.studentconsulting.util.AuthUtils;
import com.ute.studentconsulting.util.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {
    private final SortUtils sortUtils;
    private final FeedbackService feedbackService;
    private final AuthUtils authUtils;

    @DeleteMapping
    public ResponseEntity<?> deleteAllFeedback() {
        return handleDeleteAllFeedback();
    }

    private ResponseEntity<?> handleDeleteAllFeedback() {
        var user = authUtils.getCurrentUser();
        var feedback = feedbackService.findAllByUserIs(user);
        if(feedback.isEmpty()) {
            return ResponseEntity.ok(new ErrorResponse("Xóa tất cả phản hồi không thành công",
                    "Thao tác không hợp lệ. Danh sách phản hồi trống", 10091));

        }
        feedbackService.deleteAllByUser(user);
        return ResponseEntity.ok(new SuccessResponse("Xóa danh sách phản hồi thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") String id) {
        return handleDeleteFeedback(id);
    }

    private ResponseEntity<?> handleDeleteFeedback(String id) {
        var feedback = feedbackService.findById(id);
        feedbackService.deleteById(feedback.getId());
        return ResponseEntity.ok(new SuccessResponse("Xóa phản hồi thành công"));
    }

    @GetMapping
    public ResponseEntity<?> getAllFeedback(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "date, asc", name = "sort") String[] sort) {
        return handleGetAllFeedback(page, size, sort);
    }

    private ResponseEntity<?> handleGetAllFeedback(int page, int size, String[] sort) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var user = authUtils.getCurrentUser();
        var feedbackPage = feedbackService.findAllByUserIs(user, pageable);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var feedbacks = feedbackPage.getContent().stream()
                .map(feedback -> new FeedbackModel(feedback.getId(), feedback.getTitle(), feedback.getContent(),
                        simpleDateFormat.format(feedback.getDate()))).toList();
        var response = new PaginationModel<>(
                feedbacks, feedbackPage.getNumber(),
                feedbackPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }
}
