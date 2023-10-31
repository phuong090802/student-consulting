package com.ute.studentconsulting.payloads;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentPayload {
    private String id;
    private String name;
    private String description;
    private Boolean status;
}
