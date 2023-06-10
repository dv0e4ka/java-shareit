package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long booker;
    private BookingStatus status;
}
