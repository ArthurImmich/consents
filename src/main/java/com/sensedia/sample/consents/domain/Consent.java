package com.sensedia.sample.consents.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "consents")
public class Consent {
    
    @Id
    private UUID id;
    
    @CPF
    @NotBlank
    private String cpf;
    
    @NotNull
    private ConsentStatus status;
    
    @CreatedDate
    private LocalDateTime creationDateTime;
    
    private LocalDateTime expirationDateTime;
    
    @Size(min = 1, max = 50)
    private String additionalInfo;
    
}
