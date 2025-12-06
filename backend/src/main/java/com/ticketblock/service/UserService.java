package com.ticketblock.service;

import com.ticketblock.dto.Response.TicketDto;
import com.ticketblock.dto.Response.UserDto;
import com.ticketblock.dto.Response.VenueDto;
import com.ticketblock.entity.User;
import com.ticketblock.exception.ResourceNotFoundException;
import com.ticketblock.mapper.TicketMapper;
import com.ticketblock.mapper.UserMapper;
import com.ticketblock.mapper.VenueMapper;
import com.ticketblock.repository.UserRepository;
import com.ticketblock.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserById(Integer userId) {
        return userRepository.findById(userId).map(UserMapper::toDto).orElseThrow(() -> new ResourceNotFoundException("User with id:" + userId + " not found","User not found"));
    }



}
