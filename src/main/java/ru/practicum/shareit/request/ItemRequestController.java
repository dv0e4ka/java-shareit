package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.Header;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto save(@Valid @RequestBody ItemRequestDto itemRequestDto,
                               @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на сохранение заявки на вещь от пользователя id={}", userId);
        return itemRequestService.add(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на выдачу всех своих заявок от пользователя id={}", userId);
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam long from,
                                       @RequestParam @Min(0) int size) {
        log.info("поступил запрос на получение всех заявок");
        return itemRequestService.findAll(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId) {
        log.info("поступил запрос на поиск заявки по id={}", requestId);
        return itemRequestService.findById(requestId);
    }
}
