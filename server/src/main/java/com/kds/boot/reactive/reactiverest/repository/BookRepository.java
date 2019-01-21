package com.kds.boot.reactive.reactiverest.repository;

import com.kds.boot.reactive.reactiverest.model.Book;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;

/**
 * Notice that this repository extends the ReactiveMongoRepository, which support the reactive db operations.
 */
@Component
public interface BookRepository extends ReactiveMongoRepository<Book, String> {

}