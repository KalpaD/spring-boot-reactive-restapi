package com.kds.boot.reactive.reactiverest.router;

import com.kds.boot.reactive.reactiverest.handler.BookHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class BookRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(BookHandler bookHandler) {
         return RouterFunctions
                 .route(GET("/books").and(accept(MediaType.APPLICATION_JSON)), bookHandler::all)
                 .andRoute(GET("/books/{id}").and(accept(MediaType.APPLICATION_JSON)), bookHandler::get)
                 .andRoute(GET("/books/{id}/availability").and(accept(MediaType.APPLICATION_JSON)), bookHandler::getAvailability);

    }
}
