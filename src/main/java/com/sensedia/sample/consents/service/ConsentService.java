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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class ConsentService {

  private final ConsentMapper mapper;

  private final ConsentRepository repository;

  private final ConsentLogRepository logRepository;

  private final ExternalInfoClient externalInfoClient;

  public Mono<ConsentResponseDTO> create(ConsentRequestCreateDTO dto) {
    final Consent consent = mapper.toEntity(dto);
    consent.setId(UUID.randomUUID());

    return Mono.defer(
            () -> {
              if (Objects.nonNull(consent.getAdditionalInfo())) {
                log.info("additionalInfo provided in DTO. Skipping external API call.");
                return Mono.just(consent);
              }
              log.info("additionalInfo is null. Calling external API.");
              return externalInfoClient
                  .fetchAdditionalInfo()
                  .map(
                      fetchedInfo -> {
                        consent.setAdditionalInfo(fetchedInfo);
                        return consent;
                      });
            })
        .flatMap(repository::save)
        .flatMap(
            savedConsent ->
                logChange(savedConsent, ActionType.CREATED, "Consent created successfully.")
                    .thenReturn(savedConsent))
        .map(mapper::toResponseDTO);
  }

  public Mono<ConsentResponseDTO> getById(String id) {
    return repository
        .findById(UUID.fromString(id))
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("Consent not found with id: " + id)))
        .map(mapper::toResponseDTO);
  }

  public Mono<PageDTO<ConsentResponseDTO>> getAllBy(Pageable pageable) {
    return repository
        .findAllBy(pageable)
        .map(mapper::toResponseDTO)
        .collectList()
        .zipWith(repository.count())
        .map(
            tuple -> {
              List<ConsentResponseDTO> content = tuple.getT1();
              long totalElements = tuple.getT2();
              int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
              return new PageDTO<ConsentResponseDTO>(
                  content, pageable.getPageNumber(), content.size(), totalElements, totalPages);
            });
  }

  public Mono<ConsentResponseDTO> update(String id, ConsentRequestUpdateDTO dto) {
    return repository
        .findById(UUID.fromString(id))
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("Consent not found with id: " + id)))
        .map(consent -> mapper.merge(dto, consent))
        .flatMap(repository::save)
        .flatMap(
            updatedConsent ->
                logChange(updatedConsent, ActionType.UPDATED, "Consent details updated.")
                    .thenReturn(updatedConsent))
        .map(mapper::toResponseDTO);
  }

  public Mono<Void> delete(String id) {
    UUID uuid = UUID.fromString(id);
    return repository
        .findById(uuid)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException("Consent not found with id: " + id)))
        .flatMap(
            consentToRevoke ->
                repository
                    .delete(consentToRevoke)
                    .then(
                        logChange(
                            consentToRevoke, ActionType.DELETED, "Consent has been deleted.")))
        .then();
  }

  private Mono<Void> logChange(Consent consent, ActionType action, String details) {
    ConsentLog logEntry =
        ConsentLog.builder()
            .id(UUID.randomUUID())
            .consentId(consent.getId())
            .action(action)
            .details(details)
            .build();

    return logRepository
        .save(logEntry)
        .doOnSuccess(
            savedLog ->
                log.info(
                    "Successfully logged action [{}] for consent ID [{}]", action, consent.getId()))
        .doOnError(
            error ->
                log.error(
                    "Failed to log action [{}] for consent ID [{}]: {}",
                    action,
                    consent.getId(),
                    error.getMessage()))
        .then();
  }
}
