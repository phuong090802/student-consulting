package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.model.NotificationModel;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.NotificationService;
import com.ute.studentconsulting.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final AuthUtility authUtility;
    private final NotificationService notificationService;

    @PreAuthorize("hasRole('COUNSELLOR') or hasRole('DEPARTMENT_HEAD')")
    @GetMapping("/my")
    public ResponseEntity<?> getNotificationInDepartment() {
        try {
            return handleGetNotificationInDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy thông báo câu hỏi chưa trả lời: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi lấy thông báo câu hỏi chưa trả lời"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetNotificationInDepartment() {
        var department = authUtility.getCurrentUser().getDepartment();
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var notifications = notificationService.findAllByStatusIsTrueAndDepartmentIsOrderByDateDesc(department)
                .stream().map(notification ->
                        new NotificationModel(
                                notification.getId(),
                                notification.getTitle(),
                                simpleDateFormat.format(notification.getDate()),
                                notification.getQuestion().getId()));
        return ResponseEntity.ok(new ApiResponse<>(true, notifications));
    }
}
