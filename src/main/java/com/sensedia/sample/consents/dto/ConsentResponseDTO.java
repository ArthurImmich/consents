package com.sensedia.sample.consents.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sensedia.sample.consents.domain.ConsentStatus;

public record ConsentResponseDTO(

        UUID id,

        String cpf,

        ConsentStatus status,

        LocalDateTime creationDateTime,

        LocalDateTime expirationDateTime,

        String additionalInfo

) {
}