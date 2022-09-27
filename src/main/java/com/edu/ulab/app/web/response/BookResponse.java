package com.edu.ulab.app.web.response;

import lombok.Builder;
import lombok.Data;


@Data
public class BookResponse {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private long pageCount;
}
