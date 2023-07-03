package ru.practicum.shareit.booking.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long booker;
    private BookingStatus status;
}
