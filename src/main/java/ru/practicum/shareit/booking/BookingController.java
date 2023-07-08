package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.Header;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader(Header.X_SHARED_USER_ID) long requesterId,
                          @Valid @RequestBody BookingDto bookingDto) {
        return null;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patch(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                            @PathVariable("bookingId") long bookingId,
                            @RequestParam boolean approved) {
        return null;
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(Header.X_SHARED_USER_ID) long userId,
                          @PathVariable("bookingId") long bookingId) {
        return null;
    }

    @GetMapping
    public List<BookingDto> getUserBookingsByState(@RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return null;
    }

    @GetMapping("/{owner}")
    public List<BookingDto> getOwnerBookingsByState(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return null;
    }
}
