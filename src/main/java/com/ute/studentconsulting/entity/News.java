package com.ute.studentconsulting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
@RequiredArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @NonNull
    @Column(name = "title")
    private String title;

    @NonNull
    @Column(name = "content")
    private String content;

    @NonNull
    @Column(name = "date")
    private Date date;

    @Column(name = "blob_id")
    private String blobId;

    @Column(name = "file_url")
    private String fileUrl;
}
