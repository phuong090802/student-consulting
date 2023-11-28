package com.ute.studentconsulting.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FAQPayload {
    private String id;
    private String title;
    private String content;
    private String fieldId;
    private String departmentId;
}
