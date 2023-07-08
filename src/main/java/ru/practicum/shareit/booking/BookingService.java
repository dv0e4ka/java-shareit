package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    public BookingDtoResponse add(long bookerId, BookingDtoRequest bookingDtoRequest);

    public BookingDtoResponse patch(long ownerId, long bookingId, boolean approved);

    public BookingDtoResponse get(long userId, long bookingId);

    public List<BookingDtoResponse> getUserBookingsByState(long userId, String state);

    public List<BookingDtoResponse> getOwnerBookingsByState(long ownerId, String state);
}
