package com.ticketblock.dto.Response;

import com.ticketblock.entity.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
