package com.ticketblock.mapper;

import com.ticketblock.dto.Response.UserDto;
import com.ticketblock.entity.User;

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
