package com.sensedia.sample.consents.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class IExternalInfoClient implements ExternalInfoClient {

	private final WebClient webClient;
	private static final String DUMMY_API_URL = "https://jsonplaceholder.typicode.com/todos/1";
	private static final String DEFAULT_INFO = "Default information";

	public IExternalInfoClient(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public Mono<String> fetchAdditionalInfo() {
		log.info("Fetching additional info from dummy API...");

		return webClient.get()
				.uri(DUMMY_API_URL)
				.retrieve()
				.bodyToMono(Map.class)
				.map(responseBody -> Optional.ofNullable((String) responseBody.get("title")))
				.map(optionalTitle -> optionalTitle.orElse(DEFAULT_INFO))
				.onErrorResume(e -> {
					log.error("Failed to fetch from dummy API: {}", e.getMessage());
					return Mono.just(DEFAULT_INFO);
				});
	}
}