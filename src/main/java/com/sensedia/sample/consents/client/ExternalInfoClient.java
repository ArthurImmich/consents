package com.sensedia.sample.consents.client;

import reactor.core.publisher.Mono;

public interface ExternalInfoClient {
  Mono<String> fetchAdditionalInfo();
}
