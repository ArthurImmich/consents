package com.sensedia.sample.consents.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDTO(

		LocalDateTime timestamp,

		int status,

		String error,

		String message,

		String path,

		List<ValidationError> errors

) {

	public record ValidationError(

			String field,

			String message

	) {
	}

}
