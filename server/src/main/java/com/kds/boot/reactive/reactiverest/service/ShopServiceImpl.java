package com.kds.boot.reactive.reactiverest.service;

import com.kds.boot.reactive.reactiverest.model.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    private final WebClient webClient;

    public ShopServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Return a Flux with artificial delay to mimic a real world behaviour of
     * data availability over network.
     *
     * @return {@link Flux<Shop>}
     */
    @Override
    public Flux<Shop> getShopsLocal(String bookId) {

        Shop s1 = Shop.builder().name("Jeff's BookShop").currency("AUD").build();
        Shop s2 = Shop.builder().name("Randwick BookShop").currency("AUD").build();
        Shop s3 = Shop.builder().name("Sydney BookShop").currency("AUD").build();

        return Flux.just(s1, s2, s3)
                .delayElements(Duration.ofSeconds(2))
                .doOnNext( shop -> {
                    log.info("<< Shop getting available.");
                });
    }

    /**
     * Return a Flux with real world behaviour of data availability over network.
     *
     * @return {@link Flux<Shop>}
     */
    @Override
    public Flux<Shop> getShopsRemote() {
        log.info(">>> WebClient invoking the remote service.");
        Flux<Shop> shopFlux = webClient.get()
                .retrieve().bodyToFlux(Shop.class);
        log.info("<<< WebClient received the remote service response");
        return shopFlux;
    }
}
