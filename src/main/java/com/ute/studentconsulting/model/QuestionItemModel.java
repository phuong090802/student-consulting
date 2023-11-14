package com.ute.studentconsulting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionItemModel {
    private String id;
    private String title;
    private String date;
    private String fieldName;

}
