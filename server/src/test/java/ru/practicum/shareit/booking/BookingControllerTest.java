package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String URL = "/bookings";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private BookingDtoRequest bookingDtoRequest;

    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void setUp() {
        bookingDtoRequest = BookingDtoRequest
                .builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingDtoResponse = BookingDtoResponse
                .builder()
                .id(1L)
                .booker(UserDto.builder().id(1L).name("name").email("name@mail.ru").build())
                .build();

    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mvc);
    }

    @Test
    void shouldSave() throws Exception {
        when(bookingService.add(1L, bookingDtoRequest))
                .thenReturn(bookingDtoResponse);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    void shouldNotSave_WhenStartNotValid() throws Exception {
        BookingDtoRequest bookingDtoRequestNoDate = BookingDtoRequest.builder().build();

        bookingDtoRequest.setStart(null);
        when(bookingService.add(1L, bookingDtoRequestNoDate))
                .thenReturn(bookingDtoResponse);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotSave_WhenEndNotValid() throws Exception {
        BookingDtoRequest bookingDtoRequestNoDate = BookingDtoRequest.builder().build();

        bookingDtoRequest.setEnd(null);
        when(bookingService.add(1L, bookingDtoRequestNoDate))
                .thenReturn(bookingDtoResponse);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPatch() throws Exception {
        bookingDtoResponse.setStatus(BookingStatus.APPROVED);
        when(bookingService.patch(1L, 1L, true))
                .thenReturn(bookingDtoResponse);

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));

    }

    @Test
    void shouldThrowEntityNotFoundException() throws Exception {
        when(bookingService.patch(1L, 1L, true))
                .thenThrow(new EntityNotFoundException("бронь не найдена"));

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdate_WhenAlreadyApproved() throws Exception {
        when(bookingService.patch(1L, 1L, true))
                .thenThrow(new ValidationException("бронь уже потверждена"));

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetById() throws Exception {
        when(bookingService.get(1L, 1L))
                .thenReturn(bookingDtoResponse);

        mvc.perform(get(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    void shouldNotGetById_WhenNoFound() throws Exception {
        when(bookingService.get(1L, 1L))
                .thenThrow(new EntityNotFoundException("сущность не найдена"));

        mvc.perform(get(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, "1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetUserBookingByState() throws Exception {
        List<BookingDtoResponse> bookingDtoResponseList = List.of(bookingDtoResponse);

        when(bookingService.getUserBookingsByState(1L, "ALL", 0, 10))
                .thenReturn(bookingDtoResponseList);

        mvc.perform(get(URL)
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponseList)));

    }

    @Test
    void shouldNotGetUserBooking_WhenUnknownState() throws Exception {
        when(bookingService.getUserBookingsByState(1L, "UnKnownState", 0, 10))
                .thenThrow(new IllegalArgumentException("нет такого параметра State"));

        mvc.perform(get(URL)
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("state", "UnKnownState")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGetByOwnerByState() throws Exception {
        List<BookingDtoResponse> bookingDtoResponseList = List.of(bookingDtoResponse);

        when(bookingService.getOwnerBookingsByState(1L, "ALL", 0, 10))
                .thenReturn(bookingDtoResponseList);

        mvc.perform(get(URL + "/owner")
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponseList)));
    }

    @Test
    void shouldNotGetOwnerBooking_WhenUnknownState() throws Exception {
        when(bookingService.getOwnerBookingsByState(1L, "UnKnownState", 0, 10))
                .thenThrow(new IllegalArgumentException("нет такого параметра State"));

        mvc.perform(get(URL + "/owner")
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("state", "UnKnownState")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldNotGetOwnerBooking_WhenOwnerNotFound() throws Exception {
        when(bookingService.getOwnerBookingsByState(1L, "ALL", 0, 10))
                .thenThrow(new EntityNotFoundException("такого пользователя не существует"));

        mvc.perform(get(URL + "/owner")
                        .header(Header.X_SHARED_USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}