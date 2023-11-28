package com.ute.studentconsulting.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsRequest {
    private String title;
    private String content;
    private String blobId;
    private String fileUrl;
}
