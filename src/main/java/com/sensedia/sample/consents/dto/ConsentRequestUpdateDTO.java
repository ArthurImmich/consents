package com.sensedia.sample.consents.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.br.CPF;

import com.sensedia.sample.consents.domain.ConsentStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

public record ConsentRequestUpdateDTO(

		@CPF String cpf,

		ConsentStatus status,

		@Future LocalDateTime expirationDateTime,

		@Size(min = 1, max = 50) String additionalInfo

) {
}
