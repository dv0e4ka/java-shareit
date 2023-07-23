package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private long id;
    private BookingStatus status;
    private ItemDto itemDto;
    private UserDto userDto;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
}
