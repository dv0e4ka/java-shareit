package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemOutDto;

@Data
@Builder
public class ItemWithBookingInfoDto {
    private Long id;
    private String name;
    private String description;
    private long owner;
    private Boolean available;
    private BookingForItemOutDto lastBooking;
    private BookingForItemOutDto nextBooking;
}
