package com.sensedia.sample.consents.controller.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.PageDTO;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/consents")

public interface IConsentApiController {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ConsentResponseDTO> create(@Valid @RequestBody ConsentRequestCreateDTO request);

	@GetMapping(produces = { "application/json" })
	public Mono<PageDTO<ConsentResponseDTO>> getAllBy(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "creationDateTime") String sort,
			@RequestParam(defaultValue = "desc") String direction);

	@GetMapping("/{id}")
	public Mono<ConsentResponseDTO> getById(@PathVariable String id);

	@PutMapping("/{id}")
	public Mono<ConsentResponseDTO> update(
			@PathVariable String id,
			@Valid @RequestBody ConsentRequestUpdateDTO request);

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable String id);
}
