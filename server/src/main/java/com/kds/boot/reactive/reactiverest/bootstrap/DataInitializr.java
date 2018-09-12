package com.kds.boot.reactive.reactiverest.bootstrap;

import com.kds.boot.reactive.reactiverest.model.Book;
import com.kds.boot.reactive.reactiverest.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
class DataInitializr implements CommandLineRunner {

    private final BookRepository books;

    public DataInitializr(BookRepository books) {
        this.books = books;
    }

    @Override
    public void run(String[] args) {
        log.info("Start data initialization  ...");
        Book b1 = Book.builder()
                .title("BCI")
                .author("Gyle Maxwell")
                .isbn("1234-123-445-56").build();

        Book b2 = Book.builder()
                .title("EIP")
                .author("Adnan Aziz")
                .isbn("1576-33-44-56").build();

        Book b3 = Book.builder()
                .title("Algorithms")
                .author("R Sedwig")
                .isbn("1567-34-56-56").build();

        this.books
                .deleteAll()
                .thenMany(
                        Flux.just(b1, b2, b3)
                                .flatMap(book -> this.books.save(book))
                )
                .log()
                .subscribe(
                        null,
                        null,
                        () -> log.info("done initialization...")
                );

    }

}
