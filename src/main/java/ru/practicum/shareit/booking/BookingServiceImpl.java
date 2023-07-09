package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoResponse add(long bookerId, BookingDtoRequest bookingDtoRequest) {
        LocalDateTime start = bookingDtoRequest.getStart();
        LocalDateTime end = bookingDtoRequest.getEnd();
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("start should be before end");
        }
        long itemId = bookingDtoRequest.getItemId();
        bookingDtoRequest.setBookerId(bookerId);


        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("предмет не найден c id=" + itemId));

        if (!item.isAvailable()) {
            throw new ValidationException(String.format("предмет с id={} недоступен для бронирования", itemId));
        }
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь не найден c id=" + itemId));
        bookingDtoRequest.setStatus(BookingStatus.WAITING);
        Booking bookingToAdd = bookingMapper.toBooking(bookingDtoRequest, item, booker);
        Booking bookingAdded = bookingRepository.save(bookingToAdd);
        BookingDtoResponse bookingDtoResponse = bookingMapper.toBookingDtoResponse(bookingAdded);
        return bookingDtoResponse;
    }

    @Override
    public BookingDtoResponse patch(long ownerId, long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDtoResponse get(long userId, long bookingId) {
        return null;
    }

    @Override
    public List<BookingDtoResponse> getUserBookingsByState(long userId, String state) {
        return null;
    }

    @Override
    public List<BookingDtoResponse> getOwnerBookingsByState(long ownerId, String state) {
        return null;
    }
}
