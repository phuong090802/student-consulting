package com.ute.studentconsulting.utility;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.model.QuestionItemModel;
import com.ute.studentconsulting.payloads.response.ApiResponse;
import com.ute.studentconsulting.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FieldUtility {
    private final FieldService fieldService;
    private final SortUtility sortUtility;

    public ResponseEntity<?> getFieldsByIds(List<String> ids, String value, int page, int size, String[] sort) {
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
}
