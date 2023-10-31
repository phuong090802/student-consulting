package com.ute.studentconsulting.payloads;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserPayload {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String occupation;
    private String role;
}
