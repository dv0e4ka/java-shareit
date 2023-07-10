package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;

import java.util.List;


public interface ItemService {

    public ItemDto add(ItemDto itemDto);

    public ItemDto patch(ItemDto itemDto);

    public ItemWithBookingInfoDto findById(long itemId, long ownerId);

    public List<ItemWithBookingInfoDto> findAllByOwnerId(long ownerId);

    public List<ItemDto> findByParam(String text);

    public void deleteById(long id);
}