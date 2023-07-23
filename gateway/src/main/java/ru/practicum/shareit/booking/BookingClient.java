package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static String API_PREFIX = "/bookings";

    public BookingClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(long bookerId, BookingDtoRequest bookingDto) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> patch(long ownerId, long bookingId, boolean isApprove) {
        Map<String, Object> parameters = Map.of("approved", isApprove);
        return patch("/" + bookingId + "/?approved={approved}", ownerId, parameters);
    }

    public ResponseEntity<Object> get(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookingsByState(long userId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state.name(),
                "from", from,
                "size", size
        );
        return get("/?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsByOwnerByState(long ownerId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner/?state={state}&from={from}&size={size}", ownerId, parameters);
    }
}
