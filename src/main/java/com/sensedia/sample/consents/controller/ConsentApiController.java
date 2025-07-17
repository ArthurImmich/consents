package com.sensedia.sample.consents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.sensedia.sample.consents.controller.interfaces.IConsentApiController;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class ConsentApiController implements IConsentApiController {

	@Override
	public Mono<ResponseEntity<Flux<ConsentResponseDTO>>> getAll() {
		log.info("Requisição recebida!!!!!!");
		return Mono.just(ResponseEntity.ok(Flux.empty()));
	}

}
