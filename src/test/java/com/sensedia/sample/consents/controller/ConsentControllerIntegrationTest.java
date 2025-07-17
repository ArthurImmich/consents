package com.sensedia.sample.consents.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.domain.ConsentStatus;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.repository.ConsentRepository;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ConsentControllerIntegrationTest {

	private static final String API_URL = "/api/v1/consents";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ConsentRepository consentRepository;

	@Test
	@DisplayName("POST /consents - Deve criar um consentimento com sucesso e retornar 201 Created")
	void shouldCreateConsentSuccessfully() {
		ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(
				"123.456.789-09",
				ConsentStatus.ACTIVE,
				LocalDateTime.now().plusDays(30),
				"Test info");

		webTestClient.post()
				.uri(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.cpf").isEqualTo("123.456.789-09")
				.jsonPath("$.status").isEqualTo("ACTIVE");
	}

	@Test
	@DisplayName("POST /consents - Deve retornar 400 Bad Request para CPF inválido")
	void shouldReturnBadRequestForInvalidCpf() {
		ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(
				"invalid-cpf",
				ConsentStatus.ACTIVE,
				LocalDateTime.now().plusDays(30),
				"Test info");

		webTestClient.post()
				.uri(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), ConsentRequestCreateDTO.class)
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

	@Test
	@DisplayName("GET /consents/{id} - Deve encontrar um consentimento por ID e retornar 200 OK")
	void shouldFindConsentById() {
		Consent consent = Consent.builder()
				.cpf("012.345.678-90")
				.status(ConsentStatus.ACTIVE)
				.expirationDateTime(LocalDateTime.now().plusDays(30))
				.additionalInfo("Test info")
				.build();

		consent = consentRepository.save(consent).block();

		webTestClient.get()
				.uri(API_URL + "/{id}", consent.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id")
				.isEqualTo(consent.getId().toString())
				.jsonPath("$.cpf")
				.isEqualTo("012.345.678-90");
	}

	@Test
	@DisplayName("GET /consents - Deve listar todos os consentimentos")
	void shouldListAllConsents() {
		Consent consent1 = Consent.builder()
				.cpf("012.345.678-90")
				.status(ConsentStatus.ACTIVE)
				.expirationDateTime(LocalDateTime.now().plusDays(30))
				.additionalInfo("Test info1")
				.build();
		Consent consent2 = Consent.builder()
				.cpf("660.527.050-94")
				.status(ConsentStatus.ACTIVE)
				.expirationDateTime(LocalDateTime.now().plusDays(30))
				.additionalInfo("Test info2")
				.build();
		Consent consent3 = Consent.builder()
				.cpf("661.527.150-94")
				.status(ConsentStatus.REVOKED)
				.expirationDateTime(LocalDateTime.now().plusDays(30))
				.additionalInfo("Test info3")
				.build();

		consent1 = consentRepository.save(consent1).block();
		consent2 = consentRepository.save(consent2).block();
		consent3 = consentRepository.save(consent3).block();

		webTestClient.get().uri(API_URL + "?page=0&size=2")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(2);
		webTestClient.get().uri(API_URL + "?page=1&size=2")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$.length()").isEqualTo(1);
	}

	@Test
	@DisplayName("PUT /consents/{id} - Deve atualizar um consentimento e retornar 200 OK")
	void shouldUpdateConsent() {
		Consent consent = Consent.builder()
				.cpf("012.345.678-90")
				.status(ConsentStatus.ACTIVE)
				.expirationDateTime(LocalDateTime.now().plusDays(30))
				.additionalInfo("Test info")
				.build();

		consent = consentRepository.save(consent).block();

		ConsentRequestUpdateDTO request = new ConsentRequestUpdateDTO(null, ConsentStatus.REVOKED, null,
				null);

		webTestClient.put()
				.uri(API_URL + "/{id}", consent.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), ConsentRequestUpdateDTO.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.status")
				.isEqualTo(ConsentStatus.REVOKED);

		Consent updatedConsent = consentRepository.findById(consent.getId()).block();
		assertEquals(ConsentStatus.REVOKED, updatedConsent.getStatus());
	}

	@Test
	@DisplayName("DELETE /consents/{id} - Deve excluir um consentimento e retornar 204 No Content")
	void shouldRevokeConsent() {
		Consent consent = Consent.builder()
				.cpf("012.345.678-90")
				.status(ConsentStatus.ACTIVE)
				.expirationDateTime(LocalDateTime.now().plusDays(30))
				.additionalInfo("Test info")
				.build();

		consent = consentRepository.save(consent).block();

		webTestClient.delete()
				.uri(API_URL + "/{id}", consent.getId())
				.exchange()
				.expectStatus()
				.isNoContent();

		Consent deletedConsent = consentRepository.findById(consent.getId()).block();
		assertEquals(null, deletedConsent);
	}

}