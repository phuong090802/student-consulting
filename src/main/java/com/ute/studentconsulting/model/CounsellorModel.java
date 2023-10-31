package com.ute.studentconsulting.model;

import com.ute.studentconsulting.entity.Field;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CounsellorModel {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private List<Field> fields;
}
