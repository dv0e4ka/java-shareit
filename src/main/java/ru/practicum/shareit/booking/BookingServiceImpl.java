package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

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
        bookingDtoRequest.setBookerId(bookerId);
        long itemId = bookingDtoRequest.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("предмет не найден c id=" + itemId));
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
