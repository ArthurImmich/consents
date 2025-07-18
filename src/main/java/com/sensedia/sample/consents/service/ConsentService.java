package com.sensedia.sample.consents.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.PageDTO;
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

	public Mono<ConsentResponseDTO> getById(String id) {
		return repository.findById(UUID.fromString(id))
				.map(mapper::toResponseDTO);
	}

	public Mono<PageDTO<ConsentResponseDTO>> getAllBy(Pageable pageable) {
		return repository.findAllBy(pageable)
				.map(mapper::toResponseDTO)
				.collectList()
				.zipWith(repository.count())
				.map(tuple -> {
					List<ConsentResponseDTO> content = tuple.getT1();
					long totalElements = tuple.getT2();
					int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
					return new PageDTO<ConsentResponseDTO>(
							content,
							pageable.getPageNumber(),
							content.size(),
							totalElements,
							totalPages);

				});
	}

}
