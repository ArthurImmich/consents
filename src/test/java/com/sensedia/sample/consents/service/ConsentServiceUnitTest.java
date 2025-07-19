package com.sensedia.sample.consents.service;

import com.sensedia.sample.consents.client.ExternalInfoClient;
import com.sensedia.sample.consents.domain.ActionType;
import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.domain.ConsentLog;
import com.sensedia.sample.consents.domain.ConsentStatus;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.PageDTO;
import com.sensedia.sample.consents.exception.ResourceNotFoundException;
import com.sensedia.sample.consents.mapper.ConsentMapper;
import com.sensedia.sample.consents.repository.ConsentLogRepository;
import com.sensedia.sample.consents.repository.ConsentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentServiceUnitTest {

	private static final String VALID_CPF = "123.456.789-00";
	private static final UUID CONSENT_ID = UUID.randomUUID();

	@Mock
	private ConsentRepository repository;

	@Mock
	private ConsentLogRepository logRepository;

	@Mock
	private ConsentMapper mapper;

	@Mock
	private ExternalInfoClient externalInfoClient;

	@InjectMocks
	private ConsentService service;

	@Test
	@DisplayName("Should create consent without calling external API when additionalInfo is provided")
	void shouldCreateConsentWhenInfoIsProvided() {
		ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(VALID_CPF, ConsentStatus.ACTIVE,
				LocalDateTime.now().plusDays(1), "Custom Info");

		Consent consentFromMapper = Consent.builder().cpf(VALID_CPF).additionalInfo("Custom Info").build();

		Consent savedConsent = Consent.builder().id(CONSENT_ID).cpf(VALID_CPF).additionalInfo("Custom Info").build();

		ConsentResponseDTO responseDTO = new ConsentResponseDTO(CONSENT_ID, VALID_CPF, ConsentStatus.ACTIVE,
				LocalDateTime.now(), null, "Custom Info");

		when(mapper.toEntity(request)).thenReturn(consentFromMapper);
		when(repository.save(any(Consent.class))).thenReturn(Mono.just(savedConsent));
		when(logRepository.save(any(ConsentLog.class))).thenReturn(Mono.just(new ConsentLog()));
		when(mapper.toResponseDTO(savedConsent)).thenReturn(responseDTO);

		Mono<ConsentResponseDTO> resultMono = service.create(request);

		StepVerifier.create(resultMono)
				.expectNext(responseDTO)
				.verifyComplete();

		verify(externalInfoClient, never()).fetchAdditionalInfo();
		verify(repository).save(any(Consent.class));
		verify(logRepository).save(any(ConsentLog.class));
	}

	@Test
	@DisplayName("Should call external API and create consent when additionalInfo is null")
	void shouldCreateConsentWhenInfoIsNull() {
		ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(VALID_CPF, ConsentStatus.ACTIVE,
				LocalDateTime.now().plusDays(1), null);

		String fetchedInfo = "Fetched from API";

		Consent consentFromMapper = Consent.builder().cpf(VALID_CPF).additionalInfo(null).build();

		Consent savedConsent = Consent.builder().id(CONSENT_ID).cpf(VALID_CPF).additionalInfo(fetchedInfo).build();

		ConsentResponseDTO responseDTO = new ConsentResponseDTO(CONSENT_ID, VALID_CPF, ConsentStatus.ACTIVE,
				LocalDateTime.now(), null, fetchedInfo);

		when(externalInfoClient.fetchAdditionalInfo()).thenReturn(Mono.just(fetchedInfo));
		when(mapper.toEntity(request)).thenReturn(consentFromMapper);
		when(repository.save(any(Consent.class))).thenReturn(Mono.just(savedConsent));
		when(logRepository.save(any(ConsentLog.class))).thenReturn(Mono.just(new ConsentLog()));
		when(mapper.toResponseDTO(savedConsent)).thenReturn(responseDTO);

		Mono<ConsentResponseDTO> resultMono = service.create(request);

		StepVerifier.create(resultMono)
				.expectNext(responseDTO)
				.verifyComplete();

		verify(externalInfoClient, times(1)).fetchAdditionalInfo();
		verify(repository).save(any(Consent.class));
		verify(logRepository).save(any(ConsentLog.class));
	}

	@Nested
	@DisplayName("Get By ID Tests")
	class GetById {
		@Test
		@DisplayName("Should return consent when found")
		void shouldReturnConsentWhenFound() {
			Consent foundConsent = Consent.builder().id(CONSENT_ID).build();
			ConsentResponseDTO responseDTO = new ConsentResponseDTO(CONSENT_ID, null, null, null, null, null);

			when(repository.findById(CONSENT_ID)).thenReturn(Mono.just(foundConsent));
			when(mapper.toResponseDTO(foundConsent)).thenReturn(responseDTO);

			Mono<ConsentResponseDTO> resultMono = service.getById(CONSENT_ID.toString());

			StepVerifier.create(resultMono)
					.expectNext(responseDTO)
					.verifyComplete();
		}

		@Test
		@DisplayName("Should return ResourceNotFoundException when not found")
		void shouldReturnErrorWhenNotFound() {
			when(repository.findById(CONSENT_ID)).thenReturn(Mono.empty());

			Mono<ConsentResponseDTO> resultMono = service.getById(CONSENT_ID.toString());

			StepVerifier.create(resultMono)
					.expectError(ResourceNotFoundException.class)
					.verify();

			verify(mapper, never()).toResponseDTO(any());
		}
	}

	@Nested
	@DisplayName("Update Consent Tests")
	class UpdateConsent {
		@Test
		@DisplayName("Should update, log, and return consent successfully")
		void shouldUpdateAndLogConsent() {
			ConsentRequestUpdateDTO request = new ConsentRequestUpdateDTO(null, ConsentStatus.REVOKED, null, null);
			Consent existingConsent = Consent.builder().id(CONSENT_ID).status(ConsentStatus.ACTIVE).build();
			Consent mergedConsent = Consent.builder().id(CONSENT_ID).status(ConsentStatus.REVOKED).build();
			ConsentResponseDTO responseDTO = new ConsentResponseDTO(CONSENT_ID, null, ConsentStatus.REVOKED, null, null,
					null);

			when(repository.findById(CONSENT_ID)).thenReturn(Mono.just(existingConsent));
			when(mapper.merge(request, existingConsent)).thenReturn(mergedConsent);
			when(repository.save(mergedConsent)).thenReturn(Mono.just(mergedConsent));
			when(logRepository.save(any(ConsentLog.class))).thenReturn(Mono.just(new ConsentLog()));
			when(mapper.toResponseDTO(mergedConsent)).thenReturn(responseDTO);

			Mono<ConsentResponseDTO> resultMono = service.update(CONSENT_ID.toString(), request);

			StepVerifier.create(resultMono)
					.expectNext(responseDTO)
					.verifyComplete();

			ArgumentCaptor<ConsentLog> logCaptor = ArgumentCaptor.forClass(ConsentLog.class);
			verify(logRepository).save(logCaptor.capture());
			assertEquals(ActionType.UPDATED, logCaptor.getValue().getAction());
		}

		@Test
		@DisplayName("Should return ResourceNotFoundException when consent to update does not exist")
		void shouldReturnErrorWhenUpdatingNonExistent() {
			ConsentRequestUpdateDTO request = new ConsentRequestUpdateDTO(null, ConsentStatus.REVOKED, null, null);
			when(repository.findById(CONSENT_ID)).thenReturn(Mono.empty());

			Mono<ConsentResponseDTO> resultMono = service.update(CONSENT_ID.toString(), request);

			StepVerifier.create(resultMono)
					.expectError(ResourceNotFoundException.class)
					.verify();

			verify(repository, never()).save(any());
			verify(logRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("Delete Consent Tests")
	class DeleteConsent {
		@Test
		@DisplayName("Should delete and log consent successfully")
		void shouldDeleteAndLogConsent() {
			Consent existingConsent = Consent.builder().id(CONSENT_ID).build();

			when(repository.findById(CONSENT_ID)).thenReturn(Mono.just(existingConsent));
			when(repository.delete(existingConsent)).thenReturn(Mono.empty().then());
			when(logRepository.save(any(ConsentLog.class))).thenReturn(Mono.just(new ConsentLog()));

			Mono<Void> resultMono = service.delete(CONSENT_ID.toString());

			StepVerifier.create(resultMono)
					.verifyComplete();

			verify(repository).delete(existingConsent);

			ArgumentCaptor<ConsentLog> logCaptor = ArgumentCaptor.forClass(ConsentLog.class);
			verify(logRepository).save(logCaptor.capture());
			assertEquals(ActionType.DELETED, logCaptor.getValue().getAction());
		}

		@Test
		@DisplayName("Should return ResourceNotFoundException when consent to delete does not exist")
		void shouldReturnErrorWhenDeletingNonExistent() {
			when(repository.findById(CONSENT_ID)).thenReturn(Mono.empty());

			Mono<Void> resultMono = service.delete(CONSENT_ID.toString());

			StepVerifier.create(resultMono)
					.expectError(ResourceNotFoundException.class)
					.verify();

			verify(repository, never()).delete(any());
			verify(logRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("Get All Consents Tests")
	class GetAllConsents {
		@Test
		@DisplayName("Should return a paginated list of consents")
		void shouldReturnPaginatedList() {
			Pageable pageable = PageRequest.of(0, 2);
			Consent c1 = Consent.builder().id(UUID.randomUUID()).build();
			Consent c2 = Consent.builder().id(UUID.randomUUID()).build();
			List<Consent> consentList = List.of(c1, c2);
			long totalCount = 5L;

			when(repository.findAllBy(pageable)).thenReturn(Flux.fromIterable(consentList));
			when(repository.count()).thenReturn(Mono.just(totalCount));
			when(mapper.toResponseDTO(any(Consent.class))).thenAnswer(invocation -> {
				Consent c = invocation.getArgument(0);
				return new ConsentResponseDTO(c.getId(), null, null, null, null, null);
			});

			Mono<PageDTO<ConsentResponseDTO>> resultMono = service.getAllBy(pageable);

			StepVerifier.create(resultMono)
					.expectNextMatches(page -> page.content().size() == 2 &&
							page.totalElements() == totalCount &&
							page.totalPages() == 3 &&
							page.page() == 0)
					.verifyComplete();
		}
	}
}