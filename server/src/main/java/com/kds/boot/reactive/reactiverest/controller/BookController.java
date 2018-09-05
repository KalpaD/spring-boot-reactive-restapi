package com.kds.boot.reactive.reactiverest.controller;

import com.kds.boot.reactive.reactiverest.model.Book;
import com.kds.boot.reactive.reactiverest.repository.BookRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping(value = "")
    public Flux<Book> all() {
        return bookRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Book> get(@PathVariable(value = "id") long id) {
        return bookRepository.findById(Long.toString(id));
    }

    @PostMapping(value = "")
    public Mono<ServerResponse> create(@RequestBody Book book) {
        bookRepository.save(book);
        return ServerResponse.ok().build();
    }
}
