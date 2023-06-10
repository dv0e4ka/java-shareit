package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("получен запрос на добавление предмета {} владельца={}", itemDto.getName(), ownerId);
        itemDto.setOwner(ownerId);
        return itemService.add(itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long ownerId, @RequestBody ItemDto itemDto) {
        log.info("получен запрос на обновление предмета {} у владельца={}", itemDto.getName(), ownerId);
        itemDto.setOwner(ownerId);
        itemDto.setId(id);
        return itemService.patch(itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable long id) {
        log.info("поступил запрос на поиск вещи id={}", id);
        return itemService.get(id);
    }

    @GetMapping
    public List<ItemDto> getItemsAll(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("поступил запрос на поиск своих вещей от владельца id={}", ownerId);
        return itemService.getAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByParam(@RequestParam String text) {
        log.info("поступил запрос на поиск доступной вещи по параметру={}", text);
        return itemService.findByParam(text);
    }
}
