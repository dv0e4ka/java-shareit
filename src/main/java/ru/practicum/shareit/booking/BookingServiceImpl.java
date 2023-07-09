package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.UnknownStateException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Item item = findItemByIdIfExist(itemId);

        if (!item.isAvailable()) {
            throw new ValidationException(String.format("предмет с id %d недоступен для бронирования", itemId));
        }

        User booker = findUserByIdIfExist(bookerId);
        bookingDtoRequest.setStatus(BookingStatus.WAITING);
        Booking bookingToAdd = bookingMapper.toBooking(bookingDtoRequest, item, booker);
        Booking bookingAdded = bookingRepository.save(bookingToAdd);
        BookingDtoResponse bookingDtoResponse = bookingMapper.toBookingDtoResponse(bookingAdded);
        return bookingDtoResponse;
    }

    @Override
    public BookingDtoResponse patch(long ownerId, long bookingId, boolean isApproved) {
        Booking booking = findBookingByIdIfExist(bookingId);
        Item item = booking.getItem();
        long idOwnerInBooking = item.getOwner().getId();

        if (idOwnerInBooking != ownerId) {
            throw new ValidationException(
                    String.format("пользователь id %d не может дать ответ на бронь id %d, " +
                                    "так как не является владельцем предмета брони ", ownerId, bookingId));
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse get(long userId, long bookingId) {
        Booking booking = findBookingByIdIfExist(bookingId);
        long ownerId = booking.getBooker().getId();
        long bookerId = booking.getItem().getOwner().getId();
        if (userId != ownerId && userId != bookerId) {
            throw new ValidationException(String.format(
                    "пользователь id %d не может просматривать бронь id %d", userId, bookerId
            ));
        }

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getUserBookingsByState(long userId, String stateValue) {
        findUserByIdIfExist(userId);
        LocalDateTime now = LocalDateTime.now();
        List<BookingDtoResponse> bookings = new ArrayList<>();
        try {
            State state = State.valueOf(stateValue);
            switch (state) {
                case ALL:
                    bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                    break;
                case PAST:
                    bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                    break;


                case WAITING:
                    bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponse)
                            .collect(Collectors.toList());
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException("Unknown state: " + stateValue);
        }
        return bookings;
    }

    @Override
    public List<BookingDtoResponse> getOwnerBookingsByState(long ownerId, String state) {
        return null;
    }

    @Override
    public Booking findBookingByIdIfExist(long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("бронь по id %d не найдена", id)));
    }

    @Override
    public User findUserByIdIfExist(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("пользователь с id %d не найден", id)));
    }

    @Override
    public Item findItemByIdIfExist(long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("предмет с id %d не найден", id)));
    }
}
