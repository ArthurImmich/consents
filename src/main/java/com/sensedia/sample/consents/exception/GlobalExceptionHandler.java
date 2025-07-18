package com.sensedia.sample.consents.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import com.sensedia.sample.consents.dto.ErrorDTO;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(WebExchangeBindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Mono<ErrorDTO> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
		log.warn("Validation failed for request [{}]: {}", exchange.getRequest().getPath(), ex.getMessage());

		List<ErrorDTO.ValidationError> validationErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(fieldError -> new ErrorDTO.ValidationError(
						fieldError.getField(),
						fieldError.getDefaultMessage()))
				.collect(Collectors.toList());

		ErrorDTO error = new ErrorDTO(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Validation failed. Check the 'errors' field for details.",
				exchange.getRequest().getPath().value(),
				validationErrors);

		return Mono.just(error);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Mono<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex,
			ServerWebExchange exchange) {
		log.warn("Resource not found for request [{}]: {}", exchange.getRequest().getPath(), ex.getMessage());

		ErrorDTO error = new ErrorDTO(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				ex.getMessage(),
				exchange.getRequest().getPath().value(),
				null);
		return Mono.just(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Mono<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
		log.warn("Illegal argument for request [{}]: {}", exchange.getRequest().getPath(), ex.getMessage());

		ErrorDTO error = new ErrorDTO(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Invalid request parameter. Please check the request format.",
				exchange.getRequest().getPath().value(),
				null);
		return Mono.just(error);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Mono<ErrorDTO> handleGenericException(Exception ex, ServerWebExchange exchange) {
		log.error("An unexpected error occurred processing request [{}]: {}", exchange.getRequest().getPath(),
				ex.getMessage(), ex);

		ErrorDTO error = new ErrorDTO(
				LocalDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				"An unexpected error occurred. Please contact support.",
				exchange.getRequest().getPath().value(),
				null);
		return Mono.just(error);
	}
}