package com.ute.studentconsulting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailsModel {
    private String userId;
    private String name;
    private String avatar;
    private String questionId;
    private String title;
    private String content;
    private String date;
    private List<Object> answers;
}
