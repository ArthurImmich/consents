package com.sensedia.sample.consents.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.mapper.ConsentMapper;
import com.sensedia.sample.consents.repository.ConsentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@AllArgsConstructor
public class ConsentService {

	ConsentMapper mapper;

	ConsentRepository repository;

	public Mono<ConsentResponseDTO> create(ConsentRequestCreateDTO dto) {
		return Mono.just(mapper.toEntity(dto))
				.doOnNext(consent -> consent.setId(UUID.randomUUID()))
				.flatMap(repository::save)
				.map(mapper::toResponseDTO);
	}

}
