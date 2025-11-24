package com.ticketblock.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * A data transfer object used for authenticating a user.
 *
 * This class includes the necessary details required for a user registration process,
 * including the user's first name, last name, email, and password.

 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;

}
