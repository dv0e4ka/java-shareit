package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.model.EntityNotFoundException;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    User userIn;
    User userOut;
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

        userIn = User.builder()
                .name("name")
                .email("name@mail.ru")
                .build();

        userOut = User.builder()
                .id(1L)
                .name("name")
                .email("name@mail.ru")
                .build();
    }

    @Test
    void shouldAdd() {
        when(userRepository.save(userIn)).thenReturn(userOut);

        Assertions.assertEquals(userDtoOut, userService.add(userDtoIn));
    }


    @Test
    void shouldPatch() {
        UserDto userDtoToPatch = UserDto.builder().name("new name").email("newName@mail.ru").build();
        UserDto userDtoExpected = UserDto.builder().id(1L).name("new name").email("newName@mail.ru").build();
        User userToPatch = User.builder().id(1L).name("new name").email("newName@mail.ru").build();
        User userPatched = User.builder().id(1L).name("new name").email("newName@mail.ru").build();

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userOut));
        when(userRepository.save(userToPatch)).thenReturn(userPatched);

        Assertions.assertEquals(userDtoExpected, userService.patch(1L, userDtoToPatch));
    }

    @Test
    void shouldPatch_whenNameAndEmailIsBlack() {
        userIn.setId(1L);
        UserDto userDtoToPatch = UserDto.builder().name(" ").email(" ").build();

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userOut));
        when(userRepository.save(userIn)).thenReturn(userOut);

        UserDto actual = userService.patch(1L, userDtoToPatch);
        Assertions.assertEquals(userDtoOut, actual);
    }

    @Test
    void shouldPatch_whenUserNotFound_fail() {

        when(userRepository.findById(1L)).thenThrow(new EntityNotFoundException("Пользователь не найден c id="));

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.patch(1L, userDtoOut));
    }

    @Test
    void findById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userOut));

        Assertions.assertEquals(userDtoOut, userService.findById(1L));
    }

    @Test
    void findById_whenNotFound_fail() {
        when(userRepository.findById(1L)).thenThrow(new EntityNotFoundException("Пользователь не найден c id=" + 1L));

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findAll() {
        List<User> userList = List.of(userOut);
        List<UserDto> userDtoList = List.of(userDtoOut);

        when(userRepository.findAll()).thenReturn(userList);

        Assertions.assertEquals(userDtoList, userService.getAll());
    }

    @Test
    void findAll_whenEmpty() {
        List<User> userList = Collections.emptyList();;
        List<UserDto> userDtoList = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(userList);

        Assertions.assertEquals(userDtoList, userService.getAll());
    }

    @Test
    void shouldDelete() {
        userService.delete(1L);
        verify(userRepository, timeout(1)).deleteById(1L);
    }

    @Test
    void isContains() {
        when(userRepository.existsById(1L)).thenReturn(true);

        Assertions.assertTrue(userService.isContains(1L));
    }
}