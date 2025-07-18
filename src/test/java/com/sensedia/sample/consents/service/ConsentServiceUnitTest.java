package com.sensedia.sample.consents.service;

import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.domain.ConsentStatus;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.mapper.ConsentMapper;
import com.sensedia.sample.consents.repository.ConsentRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static java.util.Objects.nonNull;;

@ExtendWith(MockitoExtension.class)
class ConsentServiceUnitTest {

    @Mock
    private ConsentRepository repository;

    @Mock
    private ConsentMapper mapper;

    @InjectMocks
    private ConsentService service;

    @Test
    @DisplayName("Should call repository and return the created consent")
    void shouldBuildConsentAndCallRepository() {

        ConsentRequestCreateDTO request = new ConsentRequestCreateDTO(
                "123.456.789-09",
                ConsentStatus.ACTIVE,
                LocalDateTime.now().plusDays(30),
                "Test info");

        Consent consent = Consent.builder()
                .id(UUID.randomUUID())
                .cpf(request.cpf())
                .status(request.status())
                .creationDateTime(LocalDateTime.now())
                .expirationDateTime(request.expirationDateTime())
                .additionalInfo(request.additionalInfo())
                .build();

        ConsentResponseDTO responseDTO = new ConsentResponseDTO(
                consent.getId(),
                consent.getCpf(),
                consent.getStatus(),
                consent.getCreationDateTime(),
                consent.getExpirationDateTime(),
                consent.getAdditionalInfo());

        when(mapper.toEntity(request)).thenReturn(consent);
        when(mapper.toResponseDTO(consent)).thenReturn(responseDTO);
        when(repository.save(any(Consent.class)))
                .thenReturn(Mono.just(consent));

        Mono<ConsentResponseDTO> resultMono = service.create(request);

        StepVerifier.create(resultMono)
                .expectNextMatches(result -> {
                    return result.cpf().equals(request.cpf()) && nonNull(result.id());
                })
                .verifyComplete();

        ArgumentCaptor<Consent> consentCaptor = ArgumentCaptor.forClass(Consent.class);
        verify(repository).save(consentCaptor.capture());

        Consent consentPassedToRepository = consentCaptor.getValue();
        assertEquals(consentPassedToRepository.getCpf(), request.cpf());
    }

}