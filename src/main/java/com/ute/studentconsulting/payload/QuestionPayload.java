package com.ute.studentconsulting.payload;

import java.util.Date;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class QuestionPayload {
    private String id;
    private String title;
    private String content;
    private Date date;
    private Integer status;
    private Integer views;
    private String departmentId;
    private String fieldId;
}
