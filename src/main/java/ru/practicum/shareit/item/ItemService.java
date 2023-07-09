package ru.practicum.shareit.item;

import java.util.List;


public interface ItemService {

    public ItemDto add(ItemDto itemDto);

    public ItemDto patch(ItemDto itemDto);

    public ItemDto findById(long id);

    public List<ItemDto> findAllByUserId(long ownerId);

    public List<ItemDto> findByParam(String text);

    public void deleteById(long id);
}