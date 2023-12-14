package com.ute.studentconsulting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerApproveModel {
    private String title;
    private String content;
    private String fieldName;
    private AnswerModel answer;
    private StaffModel counsellor;

}
