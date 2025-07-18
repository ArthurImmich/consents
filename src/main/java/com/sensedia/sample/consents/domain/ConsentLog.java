package com.sensedia.sample.consents.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consent_logs")
public class ConsentLog {

	@Id
	private UUID id;

	private UUID consentId;

	private ActionType action;

	@CreatedDate
	private LocalDateTime timestamp;

	private String details;
}