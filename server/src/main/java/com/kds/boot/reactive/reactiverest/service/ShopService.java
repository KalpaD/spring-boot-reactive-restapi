package com.kds.boot.reactive.reactiverest.service;

import com.kds.boot.reactive.reactiverest.model.Shop;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;

import java.util.Map;

public interface ShopService {

    Flux<Shop> getShopsLocal(String bookId);

    Flux<Map<String, Shop>> getShopsRemote();
}
