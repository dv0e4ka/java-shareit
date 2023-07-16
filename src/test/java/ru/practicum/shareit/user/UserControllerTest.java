package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemService;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private static final String URL = "/users";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void q1() throws Exception {

    }

    @Test
    void q2() throws Exception {

    }

    @Test
    void q3() throws Exception {

    }

    @Test
    void q4() throws Exception {

    }

    @Test
    void q5() throws Exception {

    }

    @Test
    void q6() throws Exception {

    }

    @Test
    void q7() throws Exception {

    }

    @Test
    void q8() throws Exception {

    }

    @Test
    void q9() throws Exception {

    }

    @Test
    void q10() throws Exception {

    }

    @Test
    void q11() throws Exception {

    }

    @Test
    void q12() throws Exception {

    }

    @Test
    void q13() throws Exception {

    }

    @Test
    void q14() throws Exception {

    }

    @Test
    void q15() throws Exception {

    }

    @Test
    void q16() throws Exception {

    }

    @Test
    void q17() throws Exception {

    }

    @Test
    void q18() throws Exception {

    }

    @Test
    void q19() throws Exception {

    }
}