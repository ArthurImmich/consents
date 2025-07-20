package com.sensedia.sample.consents.repository;

import com.sensedia.sample.consents.domain.ConsentLog;
import java.util.UUID;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ConsentLogRepository extends ReactiveMongoRepository<ConsentLog, UUID> {}
