package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthModel {
    private String token;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String occupation;
    private String avatar;
}
