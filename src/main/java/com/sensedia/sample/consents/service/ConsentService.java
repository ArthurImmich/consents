package com.sensedia.sample.consents.service;

import com.sensedia.sample.consents.client.ExternalInfoClient;
import com.sensedia.sample.consents.domain.ActionType;
import com.sensedia.sample.consents.domain.Consent;
import com.sensedia.sample.consents.domain.ConsentLog;
import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.PageDTO;
import com.sensedia.sample.consents.exception.ResourceNotFoundException;
import com.sensedia.sample.consents.mapper.ConsentMapper;
import com.sensedia.sample.consents.repository.ConsentLogRepository;
import com.sensedia.sample.consents.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentMapper mapper;
    private final ConsentRepository repository;
    private final ConsentLogRepository logRepository;
    private final ExternalInfoClient externalInfoClient;

    public Mono<ConsentResponseDTO> create(ConsentRequestCreateDTO dto) {
        Consent consent = mapper.toEntity(dto);
        consent.setId(UUID.randomUUID());

        return fetchInfoIfNull(consent)
                .flatMap(repository::save)
                .flatMap(saved -> saveConsentLog(saved, ActionType.CREATED, "Consent created successfully."))
                .map(mapper::toResponseDTO);
    }

    private Mono<Consent> fetchInfoIfNull(Consent consent) {
        if (Objects.isNull(consent.getAdditionalInfo())) {
            log.info("Fetching additional info from external API.");
            return externalInfoClient
                    .fetchAdditionalInfo()
                    .doOnNext(consent::setAdditionalInfo)
                    .thenReturn(consent);
        }
        log.info("Additional info provided. Skipping external call.");
        return Mono.just(consent);
    }

    public Mono<ConsentResponseDTO> getById(String id) {
        return getConsent(id)
                .map(mapper::toResponseDTO);
    }

    public Mono<PageDTO<ConsentResponseDTO>> getAllBy(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(mapper::toResponseDTO)
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> {
                    List<ConsentResponseDTO> content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
                    return new PageDTO<>(content, pageable.getPageNumber(), content.size(), totalElements, totalPages);
                });
    }

    public Mono<ConsentResponseDTO> update(String id, ConsentRequestUpdateDTO dto) {
        return getConsent(id)
                .map(existing -> mapper.merge(dto, existing))
                .flatMap(repository::save)
                .flatMap(updated -> saveConsentLog(updated, ActionType.UPDATED, "Consent details updated."))
                .map(mapper::toResponseDTO);
    }

    public Mono<Void> delete(String id) {
        return getConsent(id)
                .flatMap(consent -> repository.delete(consent)
                        .then(saveConsentLog(consent, ActionType.DELETED, "Consent has been deleted.")))
                .then();
    }

    private Mono<Consent> getConsent(String id) {
        return Mono.just(id)
                .map(UUID::fromString)
                .flatMap(uuid -> repository.findById(uuid))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Consent not found with id: " + id)));
    }

    private Mono<Consent> saveConsentLog(Consent consent, ActionType action, String details) {
        ConsentLog logEntry = ConsentLog.builder()
                .id(UUID.randomUUID())
                .consentId(consent.getId())
                .action(action)
                .details(details)
                .build();

        return logRepository.save(logEntry)
                .doOnSuccess(loggedEntry -> log.info("Logged action [{}] for consent ID [{}]", action, consent.getId()))
                .doOnError(error -> log.error("Failed to log action [{}] for consent ID [{}]: {}", action,
                        consent.getId(), error.getMessage()))
                .thenReturn(consent);
    }
}
