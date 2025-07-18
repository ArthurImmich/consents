package com.sensedia.sample.consents.controller;

import org.springframework.validation.annotation.Validated;
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
	public Mono<PageDTO<ConsentResponseDTO>> getAll(int page, int size) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAll'");
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}

	@Override
	public Mono<Void> delete(String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'delete'");
	}

}
