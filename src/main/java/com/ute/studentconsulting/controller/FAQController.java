package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.FAQ;
import com.ute.studentconsulting.service.DepartmentService;
import com.ute.studentconsulting.service.FAQService;
import com.ute.studentconsulting.service.FieldService;
import com.ute.studentconsulting.util.FAQUtils;
import com.ute.studentconsulting.util.SortUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
@Slf4j
public class FAQController {
    private final FAQService faqService;
    private final SortUtils sortUtils;
    private final FieldService fieldService;
    private final DepartmentService departmentService;
    private final FAQUtils faqUtils;

    @GetMapping
    private ResponseEntity<?> getFAQs(
            @RequestParam(required = false, name = "value") String value,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size,
            @RequestParam(defaultValue = "title, asc", name = "sort") String[] sort,
            @RequestParam(defaultValue = "all", name = "departmentId") String departmentId,
            @RequestParam(defaultValue = "all", name = "fieldId") String fieldId) {
        return handleGetFAQs(value, page, size, sort, departmentId, fieldId);
    }

    private ResponseEntity<?> handleGetFAQs(String value, int page, int size, String[] sort, String departmentId, String fieldId) {
        var orders = sortUtils.sortOrders(sort);
        var pageable = PageRequest.of(page, size, Sort.by(orders));
        var faqPage = (value == null)
                ? getFAQsPage(departmentId, fieldId, pageable)
                : getFAQsPageAndSearch(value, departmentId, fieldId, pageable);
        return faqUtils.getResponseFAQ(faqPage);
    }




    // search is null, field, department is all/value
    private Page<FAQ> getFAQPageAndDepartmentIs(String departmentId, Pageable pageable) {
        // search = null, field = all, department = all
        return (departmentId.equals("all")) ? faqService.findAll(pageable)
                // search = null, field = all, department = value
                : faqService.findAllByDepartmentIs(departmentService.findByIdAndStatusIs(departmentId, true), pageable);
    }

    // search is value, field, department is all/value
    private Page<FAQ> getFAQPageAndDepartmentIsAndSearch(String value, String departmentId, Pageable pageable) {
        // search = is, field = all, department = all
        return (departmentId.equals("all")) ? faqService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(value, pageable)
                // search = null, field = all, department = value
                : faqService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs(value,
                departmentService.findByIdAndStatusIs(departmentId, true), pageable);
    }

    // value is null, field is, department is value/all
    private Page<FAQ> getFAQPageAndFieldIsAndDepartmentIs(String departmentId, String fieldId, Pageable pageable) {
        var field = fieldService.findById(fieldId);
        // value = null, department = all, field = value,
        return (departmentId.equals("all")) ? faqService.findAllByFieldIs(field, pageable)
                // value = null, department = value, field = value,
                : faqService.findAllByFieldIsAndDepartmentIs(field, departmentService.findById(departmentId), pageable);
    }

    // value is null, field is, department is value/all
    private Page<FAQ> getFAQPageAndFieldIsAndDepartmentIsAndSearch
    (String value, String departmentId, String fieldId, Pageable pageable) {
        var field = fieldService.findById(fieldId);
        // value = null, department = all, field = value,
        return (departmentId.equals("all")) ? faqService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
                (value, field, pageable)
                // value = null, department = value, field = value,
                : faqService.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndDepartmentIs
                (value, field, departmentService.findById(departmentId), pageable);
    }

    private Page<FAQ> getFAQsPageAndSearch(String value, String departmentId, String fieldId, Pageable pageable) {
        return fieldId.equals("all")
                ? getFAQPageAndDepartmentIsAndSearch(value, departmentId, pageable)
                : getFAQPageAndFieldIsAndDepartmentIsAndSearch(value, departmentId, fieldId, pageable);
    }

    private Page<FAQ> getFAQsPage(String departmentId, String fieldId, Pageable pageable) {
        return fieldId.equals("all")
                ? getFAQPageAndDepartmentIs(departmentId, pageable)
                : getFAQPageAndFieldIsAndDepartmentIs(departmentId, fieldId, pageable);
    }

}
