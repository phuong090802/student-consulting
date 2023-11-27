package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserModel {
    private String name;
    private String role;
    private String avatar;
}
