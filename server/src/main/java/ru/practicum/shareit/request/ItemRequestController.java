package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.Header;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto save(@RequestBody ItemRequestDto itemRequestDto,
                               @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на сохранение заявки на вещь от пользователя id={}", userId);
        return itemRequestService.add(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId,
                                  @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на поиск заявки по id={}", requestId);
        return itemRequestService.findById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("поступил запрос на выдачу всех своих заявок от пользователя id={}", userId);
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam (defaultValue = "0") int from,
                                       @RequestParam (defaultValue = "10") int size,
                                       @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("получен запрос на предоставление всех заявок");

        return itemRequestService.findAll(from, size, userId);
    }
}
