package com.kds.boot.reactive.reactiverest.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private String title;
    private String author;
    private String isbn;
}
