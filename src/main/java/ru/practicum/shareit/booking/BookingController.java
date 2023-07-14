package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.util.Header;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse add(@RequestHeader(Header.X_SHARED_USER_ID) long bookerId,
                                  @Valid @RequestBody BookingDtoRequest bookingDto) {
        log.info("поступил запрос на добавление бронирования от пользователя id={} " +
                "вещи id={}", bookerId, bookingDto.getId());
        return bookingService.add(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse patch(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                            @PathVariable("bookingId") long bookingId,
                            @RequestParam boolean approved) {
        log.info("поступил ответ на бронирования вещи id={} от пользователя id={}", bookingId, ownerId);
        return bookingService.patch(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse get(@RequestHeader(Header.X_SHARED_USER_ID) long userId,
                          @PathVariable("bookingId") long bookingId) {
        log.info("поступил запрос на получение информации о бронировании id={} от пользователя id={}",
                bookingId, userId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getUserBookingsByState(@Min(0) @RequestParam (defaultValue = "0") int from,
                                                           @Positive @RequestParam (defaultValue = "10") int size,
                                                           @RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("поступил запрос на получение списка всех бронирований текущего пользователя id={}", userId);
        return bookingService.getUserBookingsByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingsByOwnerByState(@Min(0) @RequestParam (defaultValue = "0") int from,
                                                              @Positive @RequestParam (defaultValue = "10") int size,
                                                              @RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                                                              @RequestParam(defaultValue = "ALL") String state) {

        log.info("поступил запрос на получение списка бронирований всех вещей владельца id={}", ownerId);

        return bookingService.getOwnerBookingsByState(ownerId, state, from, size);
    }
}
