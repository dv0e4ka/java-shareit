package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId);

    public List<ItemRequestDto> findAllByUser(long userId);

    public List<ItemRequestDto> findAll(long from, int size);

    public ItemRequestDto findById(long id);
}
