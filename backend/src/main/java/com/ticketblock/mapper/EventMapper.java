package com.ticketblock.mapper;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.entity.Event;

public class EventMapper {
    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getEventName())
                .localDate(event.getLocalDate())
                .localTime(event.getLocalTime())
                .organizer(
                        UserMapper.toDto(event.getOrganizer())
                )
                .venue(
                        VenueMapper.toDto(event.getVenue())
                )
                .standardTicketPrice(event.getStandardTicketPrice())
                .vipTicketPrice(event.getVipTicketPrice())
                .build();

    }

    public static Event toEntity(EventCreationRequest eventCreationRequest) {
        return Event.builder()
                .eventName(eventCreationRequest.getName())
                .localDate(eventCreationRequest.getLocalDate())
                .localTime(eventCreationRequest.getLocalTime())
                .standardTicketPrice(eventCreationRequest.getStandardTicketPrice())
                .vipTicketPrice(eventCreationRequest.getVipTicketPrice())
                .build();
    }
}
