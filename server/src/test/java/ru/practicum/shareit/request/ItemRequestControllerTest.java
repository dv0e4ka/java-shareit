package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static String URL = "/requests";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;
    ItemRequestDto itemRequestDtoIn;
    ItemRequestDto itemRequestDtoOut;
    List<ItemRequestDto> itemRequestDtoOutList;

    long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        itemRequestDtoIn = ItemRequestDto.builder()
                .description("топор")
                .build();

        itemRequestDtoOut = ItemRequestDto.builder()
                .id(1L)
                .requesterID(userId)
                .description("топор")
                .build();
        itemRequestDtoOutList = List.of(itemRequestDtoOut);
    }

    @Test
    void shouldSave() throws Exception {
        when(itemRequestService.add(itemRequestDtoIn, userId)).thenReturn(itemRequestDtoOut);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoOut)));
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.findById(1L, 1L)).thenReturn(itemRequestDtoOut);

        mvc.perform(get(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoOut)));
    }

    @Test
    void getAllByUser() throws Exception {
        when(itemRequestService.findAllByUser(userId)).thenReturn(itemRequestDtoOutList);

        mvc.perform(get(URL)
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoOutList)));

    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.findAll(0, 10, userId)).thenReturn(itemRequestDtoOutList);

        mvc.perform(get(URL + "/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header(Header.X_SHARED_USER_ID, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoOutList)));
    }
}