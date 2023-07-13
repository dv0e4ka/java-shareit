package ru.practicum.shareit.request;

import java.awt.print.Pageable;
import java.util.List;

public interface ItemRequestService {

    public ItemRequestDto add(ItemRequestDto itemRequestDto, long userId);

    public List<ItemRequestDto> findAllByUser(long userId);

    public ItemRequestDto findById(long id, long userId);

    public List<ItemRequestDto> findAll(int from, int size, long userId);
}
