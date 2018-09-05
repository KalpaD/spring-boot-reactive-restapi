package com.kds.boot.reactive.reactiverest.repository;

import com.kds.boot.reactive.reactiverest.model.Book;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface BookRepository extends ReactiveMongoRepository<Book, String> {

}
