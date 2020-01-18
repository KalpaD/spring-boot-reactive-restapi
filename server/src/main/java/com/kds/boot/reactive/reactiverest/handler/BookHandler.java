package com.kds.boot.reactive.reactiverest.handler;

import com.kds.boot.reactive.reactiverest.model.Book;
import com.kds.boot.reactive.reactiverest.model.Shop;
import com.kds.boot.reactive.reactiverest.repository.BookRepository;
import com.kds.boot.reactive.reactiverest.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class BookHandler {

    private final BookRepository bookRepository;
    private ShopService shopService;

    public BookHandler(BookRepository bookRepository, ShopService shopService) {
        this.bookRepository = bookRepository;
        this.shopService = shopService;
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        log.info(">>> GET /books");
        Mono<ServerResponse> response = ok().contentType(MediaType.APPLICATION_JSON)
                .body(bookRepository.findAll()
                        .doOnNext(book -> log.info("/books Book Name : " + book.getTitle())), Book.class);
        log.info("<<< GET /books");
        return response;
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info(">>> GET /books/{bookId}");
        Mono<ServerResponse> response = bookRepository.findById(id)
                .doOnNext(book -> log.info("/books/{bookId} Book Name : " + book.getTitle()))
                .flatMap(book -> ok().contentType(MediaType.APPLICATION_JSON).syncBody(book))
                .switchIfEmpty(ServerResponse.notFound().build());
        log.info("<<< GET /books/{bookId}");
        return response;
    }

    /**
     * This implementation of the getAvailability() method demonstrates a reactive flow
     * logged incorrectly and does not match with the real execution path.
     *
     * @param request
     * @return
     */
    /*public Mono<ServerResponse> getAvailability(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info(">>> GET /books/{bookId}/availability received", id);
        Flux<Shop> shops = bookRepository.findById(id)
                .doOnNext(book -> log.info("/books/{bookId}/availability Book Name : " + book.getTitle()))
                .flatMapMany(book -> shopService.getShopsRemote())
                .doOnNext(shop -> log.info("/books/{bookId}/availability Shop Name : " + shop.getName()));
        log.info("<<< GET /books/{bookId}/availability responding", id);
        return ServerResponse.ok().body(shops, Shop.class);
    }*/

    /**
     * This implementation of the getAvailability() method demonstrates a reactive flow
     * logged correctly and it matches with the real execution path.
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> getAvailability(ServerRequest request) {
        log.info(">>> GET /books/{bookId}/availability entry log");
        String id = request.pathVariable("id");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(">>> GET /books/{bookId}/availability received", id);
        Flux<Shop> shops = bookRepository.findById(id)
                .doOnNext(book -> log.info("Book Found Event : " + book.getTitle()))
                .flatMapMany(book -> {
                    log.info("Invoking shop service");
                    return shopService.getShopsRemote();
                })
                .doOnNext(shop -> {
                    log.info("Shop Found Event : " + shop.getName());
                }).doOnComplete(() -> {
                    log.info("<<< GET /books/{bookId}/availability responding", id);
                });
        return ServerResponse.ok().body(shops, Shop.class);
    }

    public Mono<ServerResponse> create(@RequestBody Book book) {
        bookRepository.save(book);
        return ok().build();
    }

}
