package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long booker;
    private BookingStatus status;
}
