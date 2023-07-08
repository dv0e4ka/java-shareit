package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoRequest {
    private long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private long itemId;

    private long bookerId;

    private BookingStatus status;
}
