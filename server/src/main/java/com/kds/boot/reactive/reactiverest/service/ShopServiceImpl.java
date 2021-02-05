package com.kds.boot.reactive.reactiverest.service;

import com.kds.boot.reactive.reactiverest.model.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
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
     * And it demonstrates a reactive flow logged incorrectly and does not match with the real execution path.
     *
     * @return {@link Flux<Shop>}
     */
    /*@Override
    public Flux<Shop> getShopsRemote() {
        log.info(">>> WebClient invoking the remote service.");
        Flux<Shop> shopFlux = webClient.get()
                .retrieve().bodyToFlux(Shop.class);
        log.info("<<< WebClient received the remote service response");
        return shopFlux;
    }*/

    /**
     * This method demonstrates a reactive flow logged correctly and it matches with the real execution path.
     * @return
     */
    @Override
    public Flux<Map<String, Shop>> getShopsRemote() {
        Hooks.onOperatorDebug();
        log.info(">>> WebClient invoking the remote service.");
        Flux<Shop> shopFlux1 = webClient.get()
                .retrieve().bodyToFlux(Shop.class)
                .doOnNext(shop -> {
                    log.info("event from WebClient1");
                })
                .doOnComplete(() -> {
                    log.info("<<< WebClient1 received the remote service response");
                });

        Flux<Shop> shopFlux2 = webClient.get()
                .retrieve().bodyToFlux(Shop.class)
                .timeout(Duration.ofSeconds(2))
                .doOnNext(shop -> {
                    log.info("event from WebClient2");
                })
                .doOnComplete(() -> {
                    log.info("<<< WebClient2 received the remote service response");
                });

        return Flux.zip(shopFlux1, shopFlux2, (shop1, shop2) -> {
            Map<String, Shop> shopMap = new LinkedHashMap<>();
            shopMap.put("shop1", shop1);
            shopMap.put("shop2", shop2);
            return shopMap;
        }).doOnComplete(() -> {
            log.info("<<< zip completed");
            Mono.error(new RuntimeException("Boom!"));
        });
    }
}
