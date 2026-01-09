package com.ticketblock.service;

import com.ticketblock.dto.Response.UserDto;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.mapper.UserMapper;
import com.ticketblock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserById(Integer userId) {
        return userRepository.findById(userId).map(UserMapper::toDto).orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " not found","User not found"));
    }



}
