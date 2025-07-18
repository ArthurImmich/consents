package com.sensedia.sample.consents.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.sensedia.sample.consents.domain.Consent;

import reactor.core.publisher.Flux;

public interface ConsentRepository extends ReactiveMongoRepository<Consent, UUID> {

	Flux<Consent> findAllBy(Pageable pageable);

}