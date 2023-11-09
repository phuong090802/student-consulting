package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.FieldUtility;
import com.ute.studentconsulting.utility.SortUtility;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.service.FieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Slf4j
public class FieldController {
    private final FieldService fieldService;
    private final SortUtility sortUtility;
    private final AuthUtility authUtility;
    private final DepartmentService departmentService;
    private final FieldUtility fieldUtility;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/departments/{id}")
    public ResponseEntity<?> getFieldsByDepartmentId(@PathVariable("id") String id) {
        try {
            return handleGetFieldsByDepartmentId(id);
        } catch (Exception e) {
            log.error("Lỗi lấy lĩnh vực theo khoa: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi lấy lĩnh vực theo khoa"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetFieldsByDepartmentId(String id) {
        var department = departmentService.findByIdAndStatusIsTrue(id);
        var ids = department.getFields().stream().map(Field::getId).toList();
        var fields = fieldService.findAllByIdIn(ids);
        return ResponseEntity.ok(new ApiResponse<>(true, fields));
    }

    @PreAuthorize("hasRole('COUNSELLOR') or hasRole('DEPARTMENT_HEAD')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyFields(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        try {
            return handleGetMyFields(value, page, size, sort);
        } catch (Exception e) {
            log.error("Lỗi sắp xếp, tìm kiếm, phân trang lĩnh vực của khoa: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi sắp xếp, tìm kiếm, phân trang lĩnh vực của khoa"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetMyFields(String value, int page, int size, String[] sort) {
        var user = authUtility.getCurrentUser();
        var ids = user.getFields().stream().map(Field::getId).toList();
        return fieldUtility.getFieldsByIds(ids, value, page, size, sort);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getField(@PathVariable("id") String id) {
        try {
            return handleGetField(id);
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm lĩnh vực: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi tìm kiếm lĩnh vực"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetField(String id) {
        var field = fieldService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, field));
    }

    @GetMapping
    public ResponseEntity<?> getFields(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "name, asc", name = "sort") String[] sort) {
        try {
            return handleGetFields(value, page, size, sort);
        } catch (Exception e) {
            log.error("Lỗi lọc, tìm kiếm, phân trang lĩnh vực: {}", e.getMessage());
            return new ResponseEntity<>(
                    new MessageResponse(false, "Lỗi lọc, tìm kiếm, phân trang lĩnh vực"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleGetFields(String value, int page, int size, String[] sort) {
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var fieldPage = (value == null)
                ? fieldService.findAll(pageable)
                : fieldService.findByNameContaining(value, pageable);
        var fields = fieldPage.getContent();
        var response =
                new PaginationModel<>(
                        fields,
                        fieldPage.getNumber(),
                        fieldPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


}
