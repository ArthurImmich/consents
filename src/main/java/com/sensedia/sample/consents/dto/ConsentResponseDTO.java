package com.sensedia.sample.consents.dto;

import java.time.LocalDateTime;

import com.sensedia.sample.consents.domain.ConsentStatus;

public record ConsentResponseDTO(

        String id,

        String cpf,

        ConsentStatus status,

        LocalDateTime creationDateTime,

        LocalDateTime expirationDateTime,

        String additionalInfo

) {
}