package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payloads.DepartmentPayload;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.SortUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {
    private final DepartmentService departmentService;
    private final SortUtility sortUtility;
    private final AuthUtility authUtility;

    @GetMapping("/all")
    public ResponseEntity<?> getAllDepartment() {
        try {
            return handleGetAllDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách khoa: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi lấy danh sách khoa"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetAllDepartment() {
        var departments = departmentService.findAllByStatusIsTrue();
        var response = departments.stream().map(department -> new DepartmentPayload(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getStatus()
        ));
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @PreAuthorize("hasRole('COUNSELLOR') or hasRole('DEPARTMENT_HEAD')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyDepartment() {
        try {
            return handleGetMyDepartment();
        } catch (Exception e) {
            log.error("Lỗi lấy khoa hiện tại: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi lấy khoa hiện tại"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable("id") String id) {
        try {
            return handleGetDepartment(id);
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm khoa: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi tìm kiếm khoa"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getDepartments(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort,
            @RequestParam(name = "status", defaultValue = "all") String status) {
        try {
            return handleGetDepartments(value, page, size, sort, status);
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm, sắp xếp, lọc, phân trang khoa: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi tìm kiếm, sắp xếp, lọc, phân trang khoa"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetDepartments(String value, int page, int size, String[] sort, String status) {
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var departmentPage = switch (status) {
            case "active" -> (value == null)
                    ? departmentService.findAllByStatusIsTrue(pageable)
                    : departmentService.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(value, pageable);
            case "inactive" -> (value == null)
                    ? departmentService.findAllByStatusIsFalse(pageable)
                    : departmentService.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse(value, pageable);
            default -> (value == null)
                    ? departmentService.findAll(pageable)
                    : departmentService.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(value, pageable);
        };

        var departments = departmentPage.getContent().stream().map(department ->
                DepartmentPayload.builder()
                        .id(department.getId())
                        .name(department.getName())
                        .description(department.getDescription())
                        .status(department.getStatus())
                        .build()
        ).toList();
        var response =
                new PaginationModel<>(
                        departments,
                        departmentPage.getNumber(),
                        departmentPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


    private ResponseEntity<?> handleGetDepartment(String id) {
        var department = departmentService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, new DepartmentPayload(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getStatus()
        )));
    }

    private ResponseEntity<?> handleGetMyDepartment() {
        var user = authUtility.getCurrentUser();
        var department = departmentService.findById(user.getDepartment().getId());
        var response = new DepartmentPayload(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getStatus());
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }
}
