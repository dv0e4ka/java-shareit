package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Header;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> add(@RequestHeader(Header.X_SHARED_USER_ID) Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("получен запрос на добавление предмета {} владельца={}", itemDto.getName(), ownerId);
        return itemClient.add(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object>  patch(@PathVariable long id,
                         @RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                         @RequestBody ItemDto itemDto) {
        log.info("получен запрос на обновление предмета {} у владельца={}", itemDto.getName(), ownerId);
        itemDto.setOwner(ownerId);
        itemDto.setId(id);
        return itemClient.patch(id, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                                  @PathVariable long itemId) {
        log.info("поступил запрос на поиск вещи id={}", itemId);
        return itemClient.findById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId) {
        log.info("поступил запрос на поиск своих вещей от владельца id={}", ownerId);
        return itemClient.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByParam(@RequestParam String text) {
        log.info("поступил запрос на поиск доступной вещи по параметру={}", text);
        return itemClient.findByParam(text);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                 @RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                 @Valid @RequestBody  CommentDto commentDto) {
        log.info("поступил запрос на добавление коммента на предмет id={}, от пользователя id={}", itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}