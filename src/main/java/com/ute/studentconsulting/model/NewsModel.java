package com.ute.studentconsulting.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsModel {
    private String id;
    private String title;
    private String content;
    private String date;
    private String fileUrl;
}
