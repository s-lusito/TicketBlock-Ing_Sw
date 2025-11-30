package com.ticketblock.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * A data transfer object used for authenticating a user.
 *
 * This class includes the email and password required for user authentication.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Not a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}
