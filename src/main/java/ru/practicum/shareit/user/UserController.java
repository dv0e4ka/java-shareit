package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto user) {
        log.info("получен запрос на добавление пользователя с именем={}, и email={}", user.getName(), user.getEmail());
        return userService.add(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto user) {
        log.info("получен запрос на обновление пользователя с id={}",id);
        return userService.update(id, user);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        log.info("получен запрос на получения пользователя id={}", id);
        return userService.get(id);
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
