package com.ute.studentconsulting.payload.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    private String content;
    private String questionId;

}
