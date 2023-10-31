package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private boolean enabled;
    private String occupation;
    private String role;
}
