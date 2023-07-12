package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId) {
        return null;
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
