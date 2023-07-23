package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto user) {
        log.info("получен запрос на добавление пользователя с именем={}, и email={}", user.getName(), user.getEmail());
        return userClient.add(user);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@PathVariable long id, @RequestBody UserDto user) {
        log.info("получен запрос на обновление пользователя с id={}",id);
        return userClient.patch(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        log.info("получен запрос на получения пользователя id={}", id);
        return userClient.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("получен запрос на получение всех пользователей");
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("получен запрос на удаление пользователя id={}", id);
        userClient.delete(id);
    }
}
