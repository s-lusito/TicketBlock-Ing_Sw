package com.ticketblock.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object used for registering a new user.
 *
 * This class includes the necessary details required for a user registration process,
 * including the user's first name, last name, email, and password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    @Email(message = "Not a valid email")
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters" )
    private String password;
    @NotNull(message = "Role is required")
    private String role;
}
