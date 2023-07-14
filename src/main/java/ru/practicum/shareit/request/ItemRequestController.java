package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.util.Header;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.awt.print.Pageable;
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
    public List<ItemRequestDto> getAll(@Positive @RequestParam (defaultValue = "0") int from,
                                       @Positive @RequestParam (defaultValue = "10") int size,
                                       @RequestHeader (Header.X_SHARED_USER_ID) long userId) {
        log.info("получен запрос на предоставление всех заявок");

        return itemRequestService.findAll(from, size, userId);
    }
}
