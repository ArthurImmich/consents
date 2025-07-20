package com.sensedia.sample.consents.dto;

import com.sensedia.sample.consents.domain.ConsentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.br.CPF;

public record ConsentRequestCreateDTO(
    @CPF @NotBlank String cpf,
    @NotNull ConsentStatus status,
    @NotNull @Future LocalDateTime expirationDateTime,
    @Size(min = 1, max = 50) String additionalInfo) {}
