package ru.practicum.shareit.user;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private static final String URL = "/users";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    UserDto userDtoIn;
    UserDto userDtoOut;

    @BeforeEach
    void setUp() {
        userDtoIn = UserDto.builder()
                .name("name")
                .email("name@mail.ru")
                .build();
        userDtoOut = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@mail.ru")
                .build();
    }

    @Test
    void shouldSave() throws Exception {
        when(userService.add(userDtoIn)).thenReturn(userDtoOut);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(userDtoOut)));
    }

    @Test
    void shouldSave_whenNameIsEmpty_fail() throws Exception {
        userDtoIn.setName("");
        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSave_whenEmailNotValid_fail() throws Exception {
        userDtoIn.setEmail("");
        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdate() throws Exception {
        when(userService.patch(1L, userDtoIn)).thenReturn(userDtoOut);

        mvc.perform(patch(URL + "/1")
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Header.X_SHARED_USER_ID, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDtoOut)));
    }

    @Test
    void findById() throws Exception {
        when(userService.findById(1L)).thenReturn(userDtoOut);

        mvc.perform(get(URL + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDtoOut)));
    }

    @Test
    void findAll() throws Exception {
        List<UserDto> userDtoList = List.of(userDtoOut);

        when(userService.getAll()).thenReturn(userDtoList);

        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDtoList)));
    }

    @Test
    void shouldDelete() throws Exception {
        mvc.perform(delete(URL + "/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}