package ru.practicum.shareit.booking;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
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
        ItemDto itemDto = ItemMapper.toDto(booking.getItem());
        UserDto userDto = UserMapper.toDto(booking.getBooker());
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemDto(itemDto)
                .userDto(userDto)
                .status(booking.getStatus())
                .build();
        return bookingDtoResponse;
    }
}
