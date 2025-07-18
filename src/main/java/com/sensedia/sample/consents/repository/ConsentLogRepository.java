package com.sensedia.sample.consents.repository;

import com.sensedia.sample.consents.domain.ConsentLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface ConsentLogRepository extends ReactiveMongoRepository<ConsentLog, UUID> {
}