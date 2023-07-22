package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.OwnerShipConflictException;
import ru.practicum.shareit.error.model.UnknownStateException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    private User owner;
    private User booker;
    private UserDto bookerDto;
    private Item item;
    private ItemDto itemDto;
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingExpected;
    private BookingDtoResponse actualBookingDtoResponse;
    private List<BookingDtoResponse> actualBookingDtoResponseList;
    private LocalDateTime now;
    private LocalDateTime start;
    private LocalDateTime end;
    private PageRequest page;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2023, 1, 1, 0, 0);
        end = LocalDateTime.of(2024, 1, 1, 0, 0);
        now = LocalDateTime.now();

        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("user@mail.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        bookerDto = UserDto.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner.getId())
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        bookingExpected = BookingDtoResponse.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto).booker(bookerDto)
                .status(BookingStatus.WAITING)
                .build();

        page = PageRequest.of(0 / 10, 10);
    }

    @Test
    void shouldSaveNewBooking() {


        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDtoResponse bookingDtoResponse = bookingService.add(2L, bookingDtoRequest);

        Assertions.assertEquals(booking.getStart(), bookingDtoResponse.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDtoResponse.getEnd());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDtoResponse.getBooker().getId());
    }

    @Test
    void saveBooking_whenItemNotExist_fail() {
        bookingDtoRequest.setItemId(2L);
        when(itemRepository.findById(2L)).thenThrow(new EntityNotFoundException("предмета нет в базе"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.add(2L, bookingDtoRequest));
        Assertions.assertEquals("предмета нет в базе", exception.getMessage());
    }

    @Test
    void saveBooking_whenUserIsOwner_fail() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        Assertions.assertThrows(
                OwnerShipConflictException.class, () -> bookingService.add(1L, bookingDtoRequest));
    }

    @Test
    void save_whenItemIsNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        Assertions.assertThrows(
                ValidationException.class, () -> bookingService.add(2L, bookingDtoRequest));
    }

    @Test
    void patch() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);


        BookingDtoResponse bookingDtoResponse = bookingService.patch(owner.getId(), booking.getId(), false);

        Assertions.assertEquals(booking.getId(), bookingDtoResponse.getId());
    }

    @Test
    void patch_whenAlreadyHasApproved_fail() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.patch(owner.getId(), booking.getId(), false));
    }

    @Test
    void shouldFindById_whenBooker() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));

        BookingDtoResponse actualBooking = bookingService.get(booker.getId(), booking.getId());
        Assertions.assertEquals(bookingExpected.getId(), actualBooking.getId());
        Assertions.assertEquals(bookingExpected, actualBooking);
    }

    @Test
    void shouldFindById_whenOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));

        actualBookingDtoResponse = bookingService.get(owner.getId(), booking.getId());
        Assertions.assertEquals(bookingExpected.getId(), actualBookingDtoResponse.getId());
        Assertions.assertEquals(bookingExpected, actualBookingDtoResponse);
    }

    @Test
    void shouldFindById_whenNoRights() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));

        Assertions.assertThrows(OwnerShipConflictException.class, () -> bookingService.get(99, booking.getId()));
    }

    @Test
    void findByState_whenAll() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getUserBookingsByState(booker.getId(), "ALL", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByState_whenCurrent() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq(page)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getUserBookingsByState(
                booker.getId(), "CURRENT", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByState_whenPAST() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), eq(page)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getUserBookingsByState(booker.getId(), "PAST", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByState_whenFUTURE() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), eq(page)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getUserBookingsByState(booker.getId(), "FUTURE", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByState_whenWAITING() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getUserBookingsByState(booker.getId(), "WAITING", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByState_whenREJECTED() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getUserBookingsByState(booker.getId(), "REJECTED", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByState_whenUnknownState_fail() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));

        Assertions.assertThrows(UnknownStateException.class, () -> bookingService.getUserBookingsByState(booker.getId(), "UnknownState", 0, 10));
    }

    @Test
    void findByState_whenUnknownUser() {
        when(userRepository.findById(booker.getId())).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> bookingService.getUserBookingsByState(booker.getId(), "ALL", 0, 10));
    }

    @Test
    void findByStateByOwner_whenAll() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getOwnerBookingsByState(booker.getId(), "ALL", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByStateByOwner_whenCurrent() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq(page)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getOwnerBookingsByState(
                booker.getId(), "CURRENT", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByStateByOwner_whenPAST() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), eq(page)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getOwnerBookingsByState(booker.getId(), "PAST", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByStateByOwner_whenFUTURE() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                eq(booker.getId()), any(LocalDateTime.class), eq(page)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getOwnerBookingsByState(booker.getId(), "FUTURE", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByStateByOwner_whenWAITING() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getOwnerBookingsByState(booker.getId(), "WAITING", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByStateByOwner_whenREJECTED() {
        List<BookingDtoResponse> expectedBookingDtoResponseList = List.of(bookingExpected);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        actualBookingDtoResponseList = bookingService.getOwnerBookingsByState(booker.getId(), "REJECTED", 0, 10);

        Assertions.assertEquals(expectedBookingDtoResponseList, actualBookingDtoResponseList);
    }

    @Test
    void findByStateByOwner_whenUnknownState_fail() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));

        Assertions.assertThrows(UnknownStateException.class, () -> bookingService.getOwnerBookingsByState(booker.getId(), "UnknownState", 0, 10));
    }

    @Test
    void findByStateByOwner_whenUnknownUser() {
        when(userRepository.findById(booker.getId())).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> bookingService.getOwnerBookingsByState(booker.getId(), "ALL", 0, 10));
    }
}