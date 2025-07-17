package com.sensedia.sample.consents.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface IConsentApi {

	@GetMapping(value = "/consents", produces = { "application/json" })
	ResponseEntity<Object> findAll();
}
