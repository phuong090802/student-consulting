package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.util.AuthUtils;
import com.ute.studentconsulting.util.FieldUtils;
import com.ute.studentconsulting.util.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {
    private final DepartmentService departmentService;
    private final SortUtils sortUtils;
    private final AuthUtils authUtils;
    private final FieldUtils fieldUtils;

    @PreAuthorize("hasRole('COUNSELLOR') or hasRole('DEPARTMENT_HEAD')")
    @GetMapping("/fields/my")
    public ResponseEntity<?> getFieldsInDepartment(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        return handleGetFieldsInDepartment(value, page, size, sort);
    }

    private ResponseEntity<?> handleGetFieldsInDepartment(String value, int page, int size, String[] sort) {
        var user = authUtils.getCurrentUser();
        var ids = user.getDepartment().getFields().stream().map(Field::getId).toList();
        return fieldUtils.getFieldsByIds(ids, value, page, size, sort);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDepartment() {
        return handleGetAllDepartment();
    }

    private ResponseEntity<?> handleGetAllDepartment() {
        var departments = departmentService.findAllByStatusIs(true);
        return ResponseEntity.ok(new ApiSuccessResponse<>(departments));
    }

    @PreAuthorize("hasRole('COUNSELLOR') or hasRole('DEPARTMENT_HEAD')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyDepartment() {
        return handleGetMyDepartment();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable("id") String id) {
        return handleGetDepartment(id);
    }

    @GetMapping
    public ResponseEntity<?> getDepartments(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort,
            @RequestParam(name = "status", defaultValue = "all") String status) {
        return handleGetDepartments(value, page, size, sort, status);
    }

    private ResponseEntity<?> handleGetDepartments(String value, int page, int size, String[] sort, String status) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var departmentPage = switch (status) {
            case "active" -> (value == null)
                    ? departmentService.findAllByStatusIs(true, pageable)
                    : departmentService.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIs(value, true, pageable);
            case "inactive" -> (value == null)
                    ? departmentService.findAllByStatusIs(false, pageable)
                    : departmentService.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIs(value, false, pageable);
            default -> (value == null)
                    ? departmentService.findAll(pageable)
                    : departmentService.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(value, pageable);
        };

        var response = new PaginationModel<>(
                departmentPage.getContent(), departmentPage.getNumber(),
                departmentPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }


    private ResponseEntity<?> handleGetDepartment(String id) {
        var department = departmentService.findById(id);
        return ResponseEntity.ok(new ApiSuccessResponse<>(department));
    }

    private ResponseEntity<?> handleGetMyDepartment() {
        var user = authUtils.getCurrentUser();
        var department = departmentService.findById(user.getDepartment().getId());
        return ResponseEntity.ok(new ApiSuccessResponse<>(department));
    }
}
