package com.ute.studentconsulting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionHomeModel {
    private String userId;
    private String name;
    private String avatar;
    private String departmentName;
    private String questionId;
    private String title;
    private int status;
    private String date;
}
