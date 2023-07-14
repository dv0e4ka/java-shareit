package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingService {

    public BookingDtoResponse add(long bookerId, BookingDtoRequest bookingDtoRequest);

    public BookingDtoResponse patch(long ownerId, long bookingId, boolean isApproved);

    public BookingDtoResponse get(long userId, long bookingId);

    //TODO: пагинация
    public List<BookingDtoResponse> getUserBookingsByState(long userId, String state, int from, int size);

    //TODO: пагинация
    public List<BookingDtoResponse> getOwnerBookingsByState(long ownerId, String state, int from, int size);

    public Booking findBookingByIdIfExist(long id);

    public User findUserByIdIfExist(long id);

    public Item findItemByIdIfExist(long id);
}
