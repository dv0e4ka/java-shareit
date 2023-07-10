package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingCommentInfoDto;

import java.util.List;


public interface ItemService {

    public ItemDto add(ItemDto itemDto);

    public ItemDto patch(ItemDto itemDto);

    public ItemWithBookingCommentInfoDto findById(long itemId, long ownerId);

    public List<ItemWithBookingCommentInfoDto> findAllByOwnerId(long ownerId);

    public List<ItemDto> findByParam(String text);

    public void deleteById(long id);

    public CommentDto saveComment(CommentDto commentDto, long userId, long itemId);
}