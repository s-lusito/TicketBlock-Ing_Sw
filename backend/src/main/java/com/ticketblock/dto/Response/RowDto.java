package com.ticketblock.dto.Response;

import com.ticketblock.entity.enumeration.RowSector;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RowDto {
    @EqualsAndHashCode.Include
    private Integer id;

    private String letter;

    private RowSector sector;

    private List<SeatDto> seats;

}
