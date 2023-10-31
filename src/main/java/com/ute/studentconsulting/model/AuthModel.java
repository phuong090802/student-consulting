package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthModel {
    private String token;
    private String name;
    private String role;
    private String avatar;
}
