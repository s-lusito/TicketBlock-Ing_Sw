package com.ticketblock.dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a standardized error response for API endpoints.
 *
 * This class is used to encapsulate details about errors encountered during API
 * request processing. It provides a structure for conveying error information back
 * to the client in a consistent manner, including details about the status, timestamp,
 * message, and specific field-related errors if applicable.
 *
 * Fields:
 * - status: The HTTP status code associated with the error.
 * - timestamp: The time at which the error response was generated.
 * - message: A general message describing the error.
 * - errors: A list of {@link ApiFieldError} objects providing details about
 *           validation or processing errors specific to individual fields.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @NotNull
    private int status;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    @Nullable
    private List<ApiFieldError> errors;
}
