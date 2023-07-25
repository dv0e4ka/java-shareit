package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.Header;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> add(@RequestHeader(Header.X_SHARED_USER_ID) long bookerId,
                                      @Valid @RequestBody BookingDtoRequest bookingDto) {
        log.info("поступил запрос на добавление бронирования от пользователя id={} " +
                "вещи id={}", bookerId, bookingDto.getId());
        return bookingClient.add(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patch(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                                    @PathVariable("bookingId") long bookingId,
                                    @RequestParam boolean approved) {
        log.info("поступил ответ на бронирования вещи id={} от пользователя id={}", bookingId, ownerId);
        return bookingClient.patch(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                  @PathVariable("bookingId") long bookingId) {
        log.info("поступил запрос на получение информации о бронировании id={} от пользователя id={}",
                bookingId, userId);
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookingsByState(@Min(0) @RequestParam (defaultValue = "0") int from,
                                                           @Positive @RequestParam (defaultValue = "10") int size,
                                                           @RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                                           @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("поступил запрос на получение списка всех бронирований текущего пользователя id={}", userId);
            return bookingClient.getUserBookingsByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerByState(@Min(0) @RequestParam (defaultValue = "0") int from,
                                                              @Positive @RequestParam (defaultValue = "10") int size,
                                                              @RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {

        log.info("поступил запрос на получение списка бронирований всех вещей владельца id={}", ownerId);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookingsByOwnerByState(ownerId, state, from, size);
    }
}