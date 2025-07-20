package com.sensedia.sample.consents.controller.interfaces;

import com.sensedia.sample.consents.dto.ConsentRequestCreateDTO;
import com.sensedia.sample.consents.dto.ConsentRequestUpdateDTO;
import com.sensedia.sample.consents.dto.ConsentResponseDTO;
import com.sensedia.sample.consents.dto.ErrorDTO;
import com.sensedia.sample.consents.dto.PageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/consents")
@Tag(
    name = "Consent Management",
    description = "Endpoints for creating, reading, updating, and revoking user consents.")
public interface IConsentApiController {

  @Operation(
      summary = "Create a new consent",
      description = "Registers a new user consent in the system and creates an initial log entry.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Consent created successfully",
        content = @Content(schema = @Schema(implementation = ConsentResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Mono<ConsentResponseDTO> create(@Valid @RequestBody ConsentRequestCreateDTO request);

  @Operation(
      summary = "List all consents",
      description = "Retrieves a paginated list of all consents.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of consents retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PageDTO.class)))
  })
  @GetMapping
  Mono<PageDTO<ConsentResponseDTO>> getAllBy(
      @Parameter(description = "Page number to retrieve (0-indexed)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page", example = "10")
          @RequestParam(defaultValue = "10")
          int size,
      @Parameter(description = "Field to sort by", example = "creationDateTime")
          @RequestParam(defaultValue = "creationDateTime")
          String sort,
      @Parameter(description = "Sort direction ('asc' or 'desc')", example = "desc")
          @RequestParam(defaultValue = "desc")
          String direction);

  @Operation(
      summary = "Get a consent by ID",
      description = "Retrieves a single consent by its unique UUID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Consent found",
        content = @Content(schema = @Schema(implementation = ConsentResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Consent not found",
        content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
  })
  @GetMapping("/{id}")
  Mono<ConsentResponseDTO> getById(
      @Parameter(
              description = "UUID of the consent to retrieve",
              example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
          @PathVariable
          String id);

  @Operation(
      summary = "Update an existing consent",
      description =
          "Updates the details of an existing consent and creates a log entry for the change.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Consent updated successfully",
        content = @Content(schema = @Schema(implementation = ConsentResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Consent not found",
        content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
  })
  @PutMapping("/{id}")
  Mono<ConsentResponseDTO> update(
      @Parameter(
              description = "UUID of the consent to update",
              example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
          @PathVariable
          String id,
      @Valid @RequestBody ConsentRequestUpdateDTO request);

  @Operation(
      summary = "Revoke a consent",
      description = "Revokes a consent by deleting it and creates a final log entry.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Consent revoked successfully"),
    @ApiResponse(
        responseCode = "404",
        description = "Consent not found",
        content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  Mono<Void> delete(
      @Parameter(
              description = "UUID of the consent to revoke",
              example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
          @PathVariable
          String id);
}
