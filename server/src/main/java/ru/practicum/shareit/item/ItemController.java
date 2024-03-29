package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingCommentInfoDto;
import ru.practicum.shareit.util.Header;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId, @RequestBody ItemDto itemDto) {
        log.info("получен запрос на добавление предмета {} владельца={}", itemDto.getName(), ownerId);
        itemDto.setOwner(ownerId);
        return itemService.add(itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@PathVariable long id,
                         @RequestHeader(Header.X_SHARED_USER_ID) long ownerId,
                         @RequestBody ItemDto itemDto) {
        log.info("получен запрос на обновление предмета {} у владельца={}", itemDto.getName(), ownerId);
        itemDto.setOwner(ownerId);
        itemDto.setId(id);
        return itemService.patch(itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingCommentInfoDto findById(@RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                                  @PathVariable long itemId) {
        log.info("поступил запрос на поиск вещи id={}", itemId);
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingCommentInfoDto> getAllItemsByOwner(@RequestHeader(Header.X_SHARED_USER_ID) long ownerId) {
        log.info("поступил запрос на поиск своих вещей от владельца id={}", ownerId);
        return itemService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByParam(@RequestParam String text) {
        log.info("поступил запрос на поиск доступной вещи по параметру={}", text);
        return itemService.findByParam(text);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(Header.X_SHARED_USER_ID) long userId,
                                 @RequestBody  CommentDto commentDto) {
        log.info("поступил запрос на добавление коммента на предмет id={}, от пользователя id={}", itemId, userId);
        return itemService.saveComment(commentDto, userId, itemId);
    }
}
