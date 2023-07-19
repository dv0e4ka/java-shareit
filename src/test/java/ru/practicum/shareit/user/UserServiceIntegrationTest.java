package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService userService;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("name")
                .email("name@mail.ru")
                .build();
    }

    @Test
    void create_whenEmailDuplicate() {
        User userIn = User.builder()
                .name("newUser")
                .email("name@mail.ru")
                .build();

        userService.add(UserMapper.toDto(user));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.add(UserMapper.toDto(userIn)));
    }
}
