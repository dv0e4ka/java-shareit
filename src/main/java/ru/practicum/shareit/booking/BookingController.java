package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.BookingDto;

//@RestController
@RequestMapping(path = "/bookings")
//@RequiredArgsConstructor
@Slf4j
public class BookingController {

//    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(BookingDto bookingDto) {
//        return bookingService.save()
        return null;
    }
}
