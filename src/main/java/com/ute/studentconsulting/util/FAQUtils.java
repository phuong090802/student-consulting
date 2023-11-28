package com.ute.studentconsulting.util;

import com.ute.studentconsulting.entity.FAQ;
import com.ute.studentconsulting.model.PaginationModel;
import com.ute.studentconsulting.payload.FAQPayload;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FAQUtils {
    public ResponseEntity<?> getResponseFAQ(Page<FAQ> faqPage) {
        var allFAQ = faqPage.getContent().stream()
                .map(faq -> new FAQPayload(
                        faq.getId(),
                        faq.getTitle(),
                        faq.getContent(),
                        faq.getField().getId(),
                        faq.getDepartment().getId()
                )).toList();
        var response = new PaginationModel<>(
                allFAQ, faqPage.getNumber(),
                faqPage.getTotalPages());
        return ResponseEntity.ok(new ApiSuccessResponse<>(response));
    }
}
