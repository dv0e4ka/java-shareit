package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId) {
        System.out.println(itemRequestDto.getDescription());
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + userId));
        ItemRequest itemRequestToSave = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        ItemRequest itemRequestAdded = itemRequestRepository.save(itemRequestToSave);
        return ItemRequestMapper.toItemRequestDto(itemRequestAdded);
    }

    @Override
    public List<ItemRequestDto> findAllByUser(long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> findAll(long from, int size) {
        return null;
    }

    @Override
    public ItemRequestDto findById(long id) {
        return null;
    }
}
