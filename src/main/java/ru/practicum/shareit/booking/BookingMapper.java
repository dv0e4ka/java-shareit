package ru.practicum.shareit.booking;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {

    public Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker) {
        return Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDtoRequest.getStatus())
                .build();
    }



    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
        return bookingDtoResponse;
    }
}
