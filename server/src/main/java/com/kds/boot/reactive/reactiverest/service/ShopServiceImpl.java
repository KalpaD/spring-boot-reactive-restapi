package com.kds.boot.reactive.reactiverest.service;

import com.kds.boot.reactive.reactiverest.model.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    private final WebClient webClient;

    public ShopServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Shop> getShops() {
        log.info(">>> WebClient invoking the remote service.");
        Flux<Shop> shopFlux = webClient.get()
                .retrieve().bodyToFlux(Shop.class).log();
        log.info("<<< WebClient received the remote service response");
        return shopFlux;
    }
}
