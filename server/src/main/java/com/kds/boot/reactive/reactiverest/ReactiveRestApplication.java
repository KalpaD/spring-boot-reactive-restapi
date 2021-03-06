package com.kds.boot.reactive.reactiverest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class ReactiveRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveRestApplication.class, args);
	}
}
