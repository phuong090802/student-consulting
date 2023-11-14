package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionModel {
    private String id;
    private String title;
    private String content;
    private String date;
    private String userId;
    private String userName;
    private String email;
    private String departmentName;
    private String fieldName;

}
