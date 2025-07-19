package com.sensedia.sample.consents.controller;

import com.sensedia.sample.consents.domain.ActionType;
import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.domain.ConsentStatus;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.PageDTO;
import com.sensedia.sample.consents.repository.ConsentLogRepository;
import com.sensedia.sample.consents.repository.ConsentRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient(timeout = "PT2H")
@ActiveProfiles("test")
class ConsentControllerIntegrationTests {

	private static final String API_URL = "/api/v1/consents";
	private static final String CPF_VALIDO_1 = "660.527.050-94";
	private static final String CPF_VALIDO_2 = "012.345.678-90";
	private static final String CPF_VALIDO_3 = "661.527.150-94";
	private static final String INFO_TESTE_1 = "Test info";

	@Container
	public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

	@DynamicPropertySource
	public static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ConsentRepository consentRepository;

	@Autowired
	private ConsentLogRepository consentLogRepository;

	@BeforeEach
	void setUp() {
		consentRepository.deleteAll().block();
		consentLogRepository.deleteAll().block();
	}

	@Test
	@DisplayName("POST /consents - Deve criar um consentimento e um log de criação")
	void shouldCreateConsentSuccessfullyAndLogIt() {
		ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(
				CPF_VALIDO_1,
				ConsentStatus.ACTIVE,
				LocalDateTime.now().plusDays(30),
				INFO_TESTE_1);

		ConsentResponseDTO createdConsent = webTestClient.post()
				.uri(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(ConsentResponseDTO.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(createdConsent);
		assertEquals(request.cpf(), createdConsent.cpf());

		StepVerifier.create(consentLogRepository.findAll())
				.expectNextMatches(log -> log.getConsentId().equals(createdConsent.id()) &&
						log.getAction() == ActionType.CREATED)
				.verifyComplete();
	}

	@Test
	@DisplayName("POST /consents - Deve retornar 400 Bad Request para CPF inválido")
	void shouldReturnBadRequestForInvalidCpf() {
		ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(
				"invalid-cpf",
				ConsentStatus.ACTIVE,
				LocalDateTime.now().plusDays(30),
				INFO_TESTE_1);

		webTestClient.post()
				.uri(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	@DisplayName("GET /consents/{id} - Deve encontrar um consentimento por ID e retornar 200 OK")
	void shouldFindConsentById() {
		Consent savedConsent = consentRepository
				.save(Consent.builder()
						.id(UUID.randomUUID())
						.cpf(CPF_VALIDO_2)
						.status(ConsentStatus.ACTIVE).build())
				.block();
		assertNotNull(savedConsent);

		webTestClient.get()
				.uri(API_URL + "/{id}", savedConsent.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(ConsentResponseDTO.class)
				.value(response -> {
					assertEquals(savedConsent.getId(), response.id());
					assertEquals(CPF_VALIDO_2, response.cpf());
				});
	}

	@Test
	@DisplayName("GET /consents/{id} - Deve retornar 404 Not Found para ID inexistente")
	void shouldReturnNotFoundForNonExistentConsent() {
		webTestClient.get()
				.uri(API_URL + "/{id}", UUID.randomUUID())
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	@DisplayName("GET /consents - Deve listar todos os consentimentos com paginação")
	void shouldListAllConsents() {
		Consent c1 = Consent.builder().id(UUID.randomUUID()).cpf(CPF_VALIDO_1).status(ConsentStatus.ACTIVE).build();
		Consent c2 = Consent.builder().id(UUID.randomUUID()).cpf(CPF_VALIDO_2).status(ConsentStatus.REVOKED).build();
		Consent c3 = Consent.builder().id(UUID.randomUUID()).cpf(CPF_VALIDO_3).status(ConsentStatus.ACTIVE).build();

		List<Consent> consentsToSave = List.of(c1, c2, c3);
		consentRepository.saveAll(consentsToSave).blockLast();

		ParameterizedTypeReference<PageDTO<ConsentResponseDTO>> pageDtoType = new ParameterizedTypeReference<>() {
		};

		webTestClient.get()
				.uri(API_URL + "?page=0&size=2")
				.exchange()
				.expectStatus().isOk()
				.expectBody(pageDtoType)
				.value(page -> {
					assertEquals(2, page.content().size());
					assertEquals(3, page.totalElements());
					assertEquals(2, page.totalPages());
				});

		webTestClient.get()
				.uri(API_URL + "?page=1&size=2")
				.exchange()
				.expectStatus().isOk()
				.expectBody(pageDtoType)
				.value(page -> {
					assertEquals(1, page.content().size());
					assertEquals(3, page.totalElements());
					assertEquals(2, page.totalPages());
				});
	}

	@Test
	@DisplayName("PUT /consents/{id} - Deve atualizar um consentimento e retornar 200 OK")
	void shouldUpdateConsent() {
		Consent savedConsent = consentRepository
				.save(Consent.builder().id(UUID.randomUUID()).cpf(CPF_VALIDO_2).status(ConsentStatus.ACTIVE).build())
				.block();
		assertNotNull(savedConsent);

		ConsentRequestUpdateDTO request = new ConsentRequestUpdateDTO(null, ConsentStatus.REVOKED, null, null);

		Consent beforeUpdatedInDb = consentRepository.findById(savedConsent.getId()).block();
		assertNotNull(beforeUpdatedInDb);
		assertEquals(ConsentStatus.ACTIVE, beforeUpdatedInDb.getStatus());
		assertNotNull(beforeUpdatedInDb.getCpf());

		webTestClient.put()
				.uri(API_URL + "/{id}", savedConsent.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk()
				.expectBody(ConsentResponseDTO.class)
				.value(response -> {
					assertEquals(ConsentStatus.REVOKED, response.status());
					assertNotNull(response.cpf());
				});

		Consent updatedInDb = consentRepository.findById(savedConsent.getId()).block();
		assertNotNull(updatedInDb);
		assertEquals(ConsentStatus.REVOKED, updatedInDb.getStatus());
		assertNotNull(updatedInDb.getCpf());

		boolean exists = consentLogRepository.findAll()
				.any(consentLog -> consentLog.getConsentId().equals(beforeUpdatedInDb.getId())
						&& consentLog.getAction() == ActionType.UPDATED)
				.block();

		assertTrue(exists);

	}

	@Test
	@DisplayName("PUT /consents/{id} - Deve retornar 404 Not Found ao tentar atualizar um consentimento inexistente")
	void shouldReturnNotFoundWhenUpdatingNonExistentConsent() {
		ConsentRequestUpdateDTO request = new ConsentRequestUpdateDTO(null, ConsentStatus.REVOKED, null, null);

		webTestClient.put()
				.uri(API_URL + "/{id}", UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	@DisplayName("PUT /consents/{id} - Deve retornar 400 Bad Request para CPF inválido")
	void shouldReturnBadRequestWhenUpdatingWithInvalidCpf() {
		Consent savedConsent = consentRepository
				.save(Consent.builder().id(UUID.randomUUID()).cpf(CPF_VALIDO_1).status(ConsentStatus.ACTIVE).build())
				.block();

		ConsentRequestUpdateDTO request = new ConsentRequestUpdateDTO("123456789", ConsentStatus.REVOKED, null, null);

		webTestClient.put()
				.uri(API_URL + "/{id}", savedConsent.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	@DisplayName("DELETE /consents/{id} - Deve excluir um consentimento e retornar 204 No Content")
	void shouldDeleteConsent() {
		Consent savedConsent = consentRepository
				.save(Consent.builder().id(UUID.randomUUID()).cpf(CPF_VALIDO_2).status(ConsentStatus.ACTIVE).build())
				.block();
		assertNotNull(savedConsent);

		webTestClient.delete()
				.uri(API_URL + "/{id}", savedConsent.getId())
				.exchange()
				.expectStatus().isNoContent();

		boolean exists = consentLogRepository.findAll()
				.any(consentLog -> consentLog.getConsentId().equals(savedConsent.getId())
						&& consentLog.getAction() == ActionType.DELETED)
				.block();

		assertTrue(exists);
	}

	@Test
	@DisplayName("DELETE /consents/{id} - Deve retornar 404 Not Found ao tentar deletar consentimento inexistente")
	void shouldReturnNotFoundWhenDeletingNonExistentConsent() {
		webTestClient.delete()
				.uri(API_URL + "/{id}", UUID.randomUUID())
				.exchange()
				.expectStatus().isNotFound();
	}
}
