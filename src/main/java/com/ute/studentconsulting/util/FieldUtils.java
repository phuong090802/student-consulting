package com.ute.studentconsulting.util;

import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FieldUtils {
    private final FieldService fieldService;
    private final SortUtils sortUtils;

    public ResponseEntity<?> getFieldsByIds(List<String> ids, String value, int page, int size, String[] sort) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var fieldPage = (value == null)
                ? fieldService.findAllByIdIn(pageable, ids)
                : fieldService.findByNameContainingIgnoreCaseAndIdIn(value, ids, pageable);
        var fields = fieldPage.getContent();
        var response = new PaginationModel<>(
                fields, fieldPage.getNumber(),
                fieldPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }
}
