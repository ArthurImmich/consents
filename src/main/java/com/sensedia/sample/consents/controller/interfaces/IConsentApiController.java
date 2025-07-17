package com.sensedia.sample.consents.controller.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sensedia.sample.consents.dto.ConsentResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/consents")
public interface IConsentApiController {

	@GetMapping(produces = { "application/json" })
	Mono<ResponseEntity<Flux<ConsentResponseDTO>>> getAll();

}
