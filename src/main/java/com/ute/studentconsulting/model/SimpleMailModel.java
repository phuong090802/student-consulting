package com.ute.studentconsulting.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleMailModel {
    private String recipient;
    private String subject;
    private String url;
}
