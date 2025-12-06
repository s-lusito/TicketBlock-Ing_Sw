package com.ticketblock.mapper;

import com.ticketblock.dto.Response.UserDto;
import com.ticketblock.entity.User;

/**
 * Mapper class for converting User entities to DTOs.
 * 
 * Provides static methods to transform User domain objects into
 * UserDto data transfer objects for API responses.
 */
public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
