package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingForItemOutDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long bookerId;
    private BookingStatus status;
}
