package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.Header;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> save(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                       @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на сохранение заявки на вещь от пользователя id={}", userId);
        return itemRequestClient.save(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable long requestId,
                                  @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на поиск заявки по id={}", requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на выдачу всех своих заявок от пользователя id={}", userId);
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@Min(0) @RequestParam (defaultValue = "0") int from,
                                       @Positive @RequestParam (defaultValue = "10") int size,
                                       @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("получен запрос на предоставление всех заявок");

        return itemRequestClient.getAll(userId, from, size);
    }
}