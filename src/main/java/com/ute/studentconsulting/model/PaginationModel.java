package com.ute.studentconsulting.model;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationModel<T> {
    private List<T> items;
    private int page;
    private int pages;
}
