package com.kds.boot.reactive.reactiverest.service;

import com.kds.boot.reactive.reactiverest.model.Shop;
import reactor.core.publisher.Flux;

public interface ShopService {

    Flux<Shop> getShops();
}
