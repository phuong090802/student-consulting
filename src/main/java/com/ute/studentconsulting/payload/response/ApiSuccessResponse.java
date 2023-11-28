package com.ute.studentconsulting.payload.response;


import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiSuccessResponse<T> {
    private boolean success = true;
    private final T data;
}
