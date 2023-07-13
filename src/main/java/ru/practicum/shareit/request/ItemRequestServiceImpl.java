package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId) {
        System.out.println(itemRequestDto.getDescription());
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + userId));
        ItemRequest itemRequestToSave = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest itemRequestAdded = itemRequestRepository.save(itemRequestToSave);
        return ItemRequestMapper.toItemRequestDto(itemRequestAdded, new ArrayList<>());
    }

    @Override
    public ItemRequestDto findById(long id) {
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
        return null;
    }

    @Override
    public List<ItemRequestDto> findAll(long from, int size) {
        return null;
    }
}
