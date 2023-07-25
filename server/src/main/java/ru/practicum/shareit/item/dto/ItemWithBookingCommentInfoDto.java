package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemOutDto;

import java.util.List;

@Data
@Builder
public class ItemWithBookingCommentInfoDto {
    private Long id;
    private String name;
    private String description;
    private long owner;
    private Boolean available;
    private BookingForItemOutDto lastBooking;
    private BookingForItemOutDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
