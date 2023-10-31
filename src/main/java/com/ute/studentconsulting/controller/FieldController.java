package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.exception.AppException;
import com.ute.studentconsulting.utility.AuthUtility;
import com.ute.studentconsulting.utility.SortUtility;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.service.FieldService;
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
    private final SortUtility sortUtility;
    private final AuthUtility authUtility;

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
            log.error("Lỗi lấy các lĩnh vực của phòng ban: {}", e.getMessage());
            throw new AppException("Lỗi lấy các lĩnh vực của phòng ban: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleGetMyFields(String value, int page, int size, String[] sort) {
        var user = authUtility.getCurrentUser();
        var ids = user.getDepartment().getFields().stream().map(Field::getId).toList();
        var orders = sortUtility.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var fieldPage = (value == null)
                ? fieldService.findAllByIdIn(pageable, ids)
                : fieldService.findByNameContainingAndIdIn(value, ids, pageable);
        var fields = fieldPage.getContent();
        var response =
                new PaginationModel<>(
                        fields,
                        fieldPage.getNumber(),
                        fieldPage.getTotalPages()
                );
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getField(@PathVariable("id") String id) {
        try {
            return handleGetField(id);
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm lĩnh vực: {}", e.getMessage());
            throw new AppException("Lỗi tìm kiếm lĩnh vực: " + e.getMessage());
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
            log.error("Lỗi lọc, phân trang lĩnh vực: {}", e.getMessage());
            throw new AppException("Lỗi lọc, phân trang lĩnh vực: " + e.getMessage());
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
