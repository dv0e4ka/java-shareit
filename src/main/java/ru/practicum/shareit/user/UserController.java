package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto user) {
        log.info("получен запрос на добавление пользователя с именем={}, и email={}", user.getName(), user.getEmail());
        return userService.add(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto user) {
        log.info("получен запрос на обновление пользователя с id={}",id);
        return userService.patch(id, user);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        log.info("получен запрос на получения пользователя id={}", id);
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("получен запрос на получение всех пользователей");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("получен запрос на удаление пользователя id={}", id);
        userService.delete(id);
    }
}
