package com.ute.studentconsulting.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FieldException extends RuntimeException {
    private String detail;
    private int code;

    public FieldException(String message, String detail, int code) {
        super(message);
        this.detail = detail;
        this.code = code;
    }

}
