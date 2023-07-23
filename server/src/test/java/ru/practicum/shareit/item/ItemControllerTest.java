package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingCommentInfoDto;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String URL = "/items";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    ItemDto itemDtoReturned;
    ItemDto itemDtoToService;
    private CommentDto commentDto;
    private ItemWithBookingCommentInfoDto itemBookingCommentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemDtoToService = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemDtoReturned = ItemDto.builder()
                .id(1L)
                .name("name")
                .owner(1L)
                .description("description")
                .available(true)
                .build();

        commentDto = CommentDto.builder().text("text").build();

        itemBookingCommentDto = ItemWithBookingCommentInfoDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

    }

    @Test
    void shouldCreateMockMvc() {
        Assertions.assertNotNull(mvc);
    }

    @Test
    void shouldSave() throws Exception {
        itemDtoToService.setOwner(1L);
        when(itemService.add(itemDtoToService)).thenReturn(itemDtoReturned);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoReturned)));
    }

    @Test
    void shouldPatch() throws Exception {
        itemDtoToService.setOwner(1L);
        itemDtoToService.setId(1L);

        when(itemService.patch(itemDtoToService)).thenReturn(itemDtoReturned);

        mvc.perform(patch(URL + "/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoReturned)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindById() throws Exception {

        when(itemService.findById(1L, 1L)).thenReturn(itemBookingCommentDto);

        mvc.perform(get(URL + "/1")
                        .header(Header.X_SHARED_USER_ID, 1))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(itemBookingCommentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllItemsByOwner() throws Exception {
        List<ItemWithBookingCommentInfoDto> itemWithBookingCommentInfoDtoList = List.of(itemBookingCommentDto);

        when(itemService.findAllByOwnerId(1L)).thenReturn(itemWithBookingCommentInfoDtoList);

        mvc.perform(get(URL)
                        .header(Header.X_SHARED_USER_ID, 1))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(itemWithBookingCommentInfoDtoList)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetByParam() throws Exception {
        List<ItemDto> itemDtoList = List.of(itemDto);

        when(itemService.findByParam("text")).thenReturn(itemDtoList);

        mvc.perform(get(URL + "/search")
                .param("text", "text"))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoList)))
                .andExpect(status().isOk());
    }
}