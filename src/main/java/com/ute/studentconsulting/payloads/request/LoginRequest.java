package com.ute.studentconsulting.payloads.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
