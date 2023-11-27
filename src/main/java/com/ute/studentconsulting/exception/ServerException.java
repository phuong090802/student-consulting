package com.ute.studentconsulting.exception;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServerException extends RuntimeException {
    private String detail;
    private int code;

    public ServerException(String message, String detail, int code) {
        super(message);
        this.detail = detail;
        this.code = code;
    }
}