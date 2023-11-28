package com.ute.studentconsulting.payloads.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SuccessResponse {
    private boolean success = true;
    private final String message;
}
