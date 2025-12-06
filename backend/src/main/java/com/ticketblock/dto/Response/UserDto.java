package com.ticketblock.dto.Response;

import com.ticketblock.entity.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User information in API responses.
 * 
 * This DTO is used to transfer user data including name, email,
 * and role information without exposing sensitive fields like passwords.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

}
