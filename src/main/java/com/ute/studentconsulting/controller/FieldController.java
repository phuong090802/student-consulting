package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FieldService;
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
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Slf4j
public class FieldController {
    private final FieldService fieldService;
    private final SortUtils sortUtils;
    private final AuthUtils authUtils;
    private final DepartmentService departmentService;
    private final FieldUtils fieldUtils;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/departments/{id}")
    public ResponseEntity<?> getFieldsByDepartmentId(@PathVariable("id") String id) {
        return handleGetFieldsByDepartmentId(id);
    }

    private ResponseEntity<?> handleGetFieldsByDepartmentId(String id) {
        var department = departmentService.findByIdAndStatusIs(id, true);
        var ids = department.getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdInAndStatusIs(ids, true);
        return ResponseEntity.ok(new ApiSuccessResponse<>(fields));
    }

    @PreAuthorize("hasRole('COUNSELLOR') or hasRole('DEPARTMENT_HEAD')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyFields(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        return handleGetMyFields(value, page, size, sort);
    }

    private ResponseEntity<?> handleGetMyFields(String value, int page, int size, String[] sort) {
        var user = authUtils.getCurrentUser();
        var ids = user.getFields().stream().map(Field::getId).toList();
        return fieldUtils.getFieldsByIds(ids, value, page, size, sort);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getField(@PathVariable("id") String id) {
        return handleGetField(id);
    }

    private ResponseEntity<?> handleGetField(String id) {
        var field = fieldService.findById(id);
        return ResponseEntity.ok(new ApiSuccessResponse<>(field));
    }

    @GetMapping
    public ResponseEntity<?> getFields(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort,
            @RequestParam(name = "status", defaultValue = "all") String status) {
        return handleGetFields(value, page, size, sort, status);
    }

    private ResponseEntity<?> handleGetFields(String value, int page, int size, String[] sort, String status) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var fieldPage = switch (status) {
            case "active" -> (value == null)
                    ? fieldService.findAllByStatusIs(true, pageable)
                    : fieldService.findByNameContainingIgnoreCaseAndStatusIs(value, true, pageable);
            case "inactive" -> (value == null)
                    ? fieldService.findAllByStatusIs(false, pageable)
                    : fieldService.findByNameContainingIgnoreCaseAndStatusIs(value, false, pageable);
            default -> (value == null)
                    ? fieldService.findAll(pageable)
                    : fieldService.findByNameContainingIgnoreCase(value, pageable);
        };
        var fields = fieldPage.getContent();
        var response = new PaginationModel<>(
                fields, fieldPage.getNumber(),
                fieldPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }

}
