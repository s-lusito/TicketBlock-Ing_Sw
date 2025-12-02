package com.ticketblock.mapper;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.entity.Event;

public class EventMapper {
    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getEventName())
                .date(event.getDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .organizer(
                        UserMapper.toDto(event.getOrganizer())
                )
                .venue(
                        VenueMapper.toSummaryDto(event.getVenue())
                )
                .standardTicketPrice(event.getStandardTicketPrice())
                .vipTicketPrice(event.getVipTicketPrice())
                .imageUrl(event.getImageUrl())
                .build();

    }

    public static Event toEntity(EventCreationRequest eventCreationRequest) {
        return Event.builder()
                .eventName(eventCreationRequest.getName())
                .date(eventCreationRequest.getDate())
                .endTime(eventCreationRequest.getEndTime())
                .startTime(eventCreationRequest.getStartTime())
                .standardTicketPrice(eventCreationRequest.getStandardTicketPrice())
                .vipTicketPrice(eventCreationRequest.getVipTicketPrice())
                .imageUrl(eventCreationRequest.getImageUrl())
                .build();
    }
}
