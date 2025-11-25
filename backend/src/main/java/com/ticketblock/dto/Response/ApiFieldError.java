package com.ticketblock.dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a field-specific error in an API response.
 *
 * This class is used to encapsulate details about validation or processing
 * errors related to a specific field when handling API requests. It provides
 * information about which field encountered an issue, the corresponding error
 * message, and optionally the rejected value that caused the error.
 *
 * This class is typically used as part of an {@link ErrorResponse} to provide
 * detailed error information in API responses.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiFieldError {
    @NotNull
    private String field;
    @NotNull
    private String message;
    @Nullable
    private Object rejectedValue;
}
