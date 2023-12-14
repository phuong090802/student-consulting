package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserModel {
    private String name;
    private String email;
    private String phone;
    private String role;
    private String occupation;
    private String avatar;
}
