package com.ute.studentconsulting.payloads.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponse {
    private boolean success = false;
    private final String message;
    private final String detail;
    private final int code;

}
