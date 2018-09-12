package com.kds.boot.reactive.reactiverest.controller;

import com.kds.boot.reactive.reactiverest.model.Book;
import com.kds.boot.reactive.reactiverest.model.Shop;
import com.kds.boot.reactive.reactiverest.repository.BookRepository;
import com.kds.boot.reactive.reactiverest.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/books")
@Slf4j
public class BookController {

    private final BookRepository bookRepository;
    private ShopService shopService;

    public BookController(BookRepository bookRepository, ShopService shopService) {
        this.bookRepository = bookRepository;
        this.shopService = shopService;
    }

    @GetMapping(value = "")
    public Flux<Book> all() {
        return bookRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Book> get(@PathVariable(value = "id") long id) {
        return bookRepository.findById(Long.toString(id));
    }

    @GetMapping(value = "/{id}/availability")
    public Flux<Shop> getAvilability(@PathVariable(value = "id") long id) {
        log.info(">>> GET /books/{}/availability received", id);
        Flux<Shop> shops = shopService.getShops();
        log.info("<<< GET /books/{}/availability responding", id);
        return shops;
    }

    @PostMapping(value = "")
    public Mono<ServerResponse> create(@RequestBody Book book) {
        bookRepository.save(book);
        return ServerResponse.ok().build();
    }
}
