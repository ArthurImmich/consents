package com.sensedia.sample.consents.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.sensedia.sample.consents.domain.Consent;

public interface ConsentRepository extends ReactiveMongoRepository<Consent, UUID> {
}