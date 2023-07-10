package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.OwnerShipConflictException;
import ru.practicum.shareit.error.model.UnknownStateException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
        if (item.getOwner().getId() == bookerId) {
            throw new OwnerShipConflictException(
                    String.format("пользователь с id %d не может бронировать свой предмет %d", bookerId, itemId));
        }

        if (!item.isAvailable()) {
            throw new ValidationException(String.format("предмет с id %d недоступен для бронирования", itemId));
        }

        User booker = findUserByIdIfExist(bookerId);
        bookingDtoRequest.setStatus(BookingStatus.WAITING);
        Booking bookingToAdd = BookingMapper.toBooking(bookingDtoRequest, item, booker);
        Booking bookingAdded = bookingRepository.save(bookingToAdd);
        return BookingMapper.toBookingDtoResponse(bookingAdded);
    }

    @Override
    public BookingDtoResponse patch(long ownerId, long bookingId, boolean isApproved) {
        Booking booking = findBookingByIdIfExist(bookingId);
        Item item = booking.getItem();
        long idOwnerInBooking = item.getOwner().getId();

        if (idOwnerInBooking != ownerId) {
            throw new OwnerShipConflictException(
                    String.format("пользователь id %d не может дать ответ на бронь id %d, " +
                                    "так как не является владельцем предмета брони ", ownerId, bookingId));
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException(
                    String.format("бронь id %d уже имеет статус APPROVED", bookingId)
            );
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
            throw new OwnerShipConflictException(String.format(
                    "пользователь id %d не может просматривать бронь id %d", userId, bookerId
            ));
        }

        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getUserBookingsByState(long userId, String stateValue) {
        findUserByIdIfExist(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        try {
            State state = State.valueOf(stateValue);
            switch (state) {
                case ALL:
                    bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                            userId, now, now );
                    break;
                case PAST:
                    bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                            userId, now);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                            userId, now);
                    break;
                case WAITING:
                    bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                            userId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                            userId, BookingStatus.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException("Unknown state: " + stateValue);
        }
        return bookings.stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getOwnerBookingsByState(long ownerId, String stateValue) {
        findUserByIdIfExist(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        try {
            State state = State.valueOf(stateValue);
            switch (state) {
                case ALL:
                    bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                            ownerId, now, now);
                    break;
                case PAST:
                    bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                            ownerId, now);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                            ownerId, now);
                    break;

                case WAITING:
                    bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                            ownerId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                            ownerId, BookingStatus.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException("Unknown state: " + stateValue);
        }
        return bookings.stream().map(BookingMapper::toBookingDtoResponse).collect(Collectors.toList());
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
