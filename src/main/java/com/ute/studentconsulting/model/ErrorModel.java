package com.ute.studentconsulting.model;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel {
    private HttpStatus status;
    private String message;
}
