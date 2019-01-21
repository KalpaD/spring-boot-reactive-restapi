package com.kds.boot.reactive.reactiverest.controller;

import com.kds.boot.reactive.reactiverest.model.Book;
import com.kds.boot.reactive.reactiverest.model.Shop;
import com.kds.boot.reactive.reactiverest.repository.BookRepository;
import com.kds.boot.reactive.reactiverest.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    public Mono<Book> get(@PathVariable(value = "id") String id) {
        return bookRepository.findById(id);
    }

    /**
     * The results of this endpoint is simulated via an artificial network delay by slowdown the shop service response.
     *
     * @param id The book id
     * @return {@link Flux<Shop>}
     */
    @GetMapping(value = "/{id}/shops", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Shop> getShops(@PathVariable(value = "id") String id) {
        log.info(">>> GET /books/{id}/availability received", id);
        return bookRepository.findById(id)
                .flatMapMany( book -> shopService.getShopsLocal(book.getId()));
    }

    /**
     * Create a book in the book db.
     *
     * @param book The book details.
     * @return The status of the book creation.
     */
    @PostMapping(value = "")
    public Mono<ServerResponse> create(@RequestBody Book book) {
        bookRepository.save(book);
        return ServerResponse.ok().build();
    }
}
