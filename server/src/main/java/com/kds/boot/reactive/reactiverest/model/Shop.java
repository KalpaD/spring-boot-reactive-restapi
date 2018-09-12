package com.kds.boot.reactive.reactiverest.model;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {

    private String name;
    private String currency;
}
