package com.ute.studentconsulting.payloads.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    private String content;
    private String questionId;

}
