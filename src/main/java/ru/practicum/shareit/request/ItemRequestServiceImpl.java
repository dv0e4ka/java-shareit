package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + userId));
        ItemRequest itemRequestToSave = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest itemRequestAdded = itemRequestRepository.save(itemRequestToSave);
        return ItemRequestMapper.toItemRequestDto(itemRequestAdded, new ArrayList<>());
    }

    @Override
    public ItemRequestDto findById(long id, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("запрос не найден c id=" + id));
        List<ItemDto> items = itemRepository.findAllByRequestId(id).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> findAllByUser(long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + userId));
        List<ItemRequest> itemRequestList = itemRequestRepository.findByRequesterIdOrderById(userId);
        return setItemsInfo(itemRequestList);
    }

    private List<ItemRequestDto> setItemsInfo(List<ItemRequest> itemRequestList) {
        List<Long> itemRequestIds = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());

        List<ItemDto> itemDtoList = itemRepository.findAllByRequestIdIn(itemRequestIds).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemMapByRequestId = itemDtoList.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        if (!itemMapByRequestId.isEmpty()) {
            itemRequestList.forEach(itemRequest -> {
                long id = itemRequest.getId();
                if (itemMapByRequestId.containsKey(id)) {
                    List<ItemDto> items = itemMapByRequestId.get(id);
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, items);
                    itemRequestDtoList.add(itemRequestDto);
                }
            });
        } else {
            itemRequestList.forEach(itemRequest -> {
                ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>());
                itemRequestDtoList.add(itemRequestDto);
            });
        }
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> findAll(long from, int size) {
        return null;
    }
}
