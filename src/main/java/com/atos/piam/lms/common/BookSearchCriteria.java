package com.atos.piam.lms.common;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookSearchCriteria {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private String language;
    private Boolean availability;
}