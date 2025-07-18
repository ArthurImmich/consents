package com.sensedia.sample.consents.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sensedia.sample.consents.controller.interfaces.IConsentApiController;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.PageDTO;
import com.sensedia.sample.consents.service.ConsentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ConsentApiController implements IConsentApiController {

	private final ConsentService service;

	@Override
	public Mono<PageDTO<ConsentResponseDTO>> getAllBy(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "creationDateTime") String sort,
			@RequestParam(defaultValue = "desc") String direction) {

		Sort sortObj = direction.equalsIgnoreCase("asc")
				? Sort.by(sort).ascending()
				: Sort.by(sort).descending();

		Pageable pageable = PageRequest.of(page, size, sortObj);

		return service.getAllBy(pageable);
	}

	@Override
	public Mono<ConsentResponseDTO> create(@Valid ConsentRequestCreateDTO request) {
		return service.create(request);
	}

	@Override
	public Mono<ConsentResponseDTO> getById(String id) {
		return service.getById(id);
	}

	@Override
	public Mono<ConsentResponseDTO> update(String id, @Valid ConsentRequestUpdateDTO request) {
		return service.update(id, request);
	}

	@Override
	public Mono<Void> delete(String id) {
		return service.delete(id);
	}

}
