package com.ute.studentconsulting.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConflictException extends RuntimeException {
    private String message;
    private String detail;
    private int code;
}
