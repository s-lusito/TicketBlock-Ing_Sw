package com.ticketblock.dto.Response;

import com.ticketblock.entity.enumeration.RowSector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for Row information in API responses.
 * 
 * This DTO is used to transfer row data including the row letter,
 * sector classification (STANDARD or VIP), and associated seats.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RowDto {
    private Integer id;

    private String letter;

    private RowSector sector;

    private List<SeatDto> seats;

}
