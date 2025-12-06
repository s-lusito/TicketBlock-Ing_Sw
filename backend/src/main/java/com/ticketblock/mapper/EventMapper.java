package com.ticketblock.mapper;

import com.ticketblock.dto.Request.EventCreationRequest;
import com.ticketblock.dto.Response.EventDto;
import com.ticketblock.entity.Event;
import com.ticketblock.utils.MoneyHelper;

public class EventMapper {
    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .eventSaleStatus(event.getSaleStatus())
                .saleStartDate(event.getSaleStartDate())
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
                .name(eventCreationRequest.getName())
                .date(eventCreationRequest.getDate())
                .endTime(eventCreationRequest.getEndTime())
                .startTime(eventCreationRequest.getStartTime())
                .saleStartDate(eventCreationRequest.getSaleStartDate())
                .standardTicketPrice(MoneyHelper.normalizeAmount(eventCreationRequest.getStandardTicketPrice()))
                .vipTicketPrice(MoneyHelper.normalizeAmount(eventCreationRequest.getVipTicketPrice())) //normalize the amount
                .imageUrl(eventCreationRequest.getImageUrl())
                .build();
    }
}
