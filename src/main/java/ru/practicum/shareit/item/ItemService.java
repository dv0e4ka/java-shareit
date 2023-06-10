package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemDao itemDao;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemDao itemDao, UserService userService, ItemMapper itemMapper) {
        this.itemDao = itemDao;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public ItemDto add(ItemDto itemDto) {
        long owner = itemDto.getOwner();
        if (!userService.isContains(owner)) {
            throw new EntityNotFoundException("id владельца=" + owner + " предмета " + itemDto.getName() + " не найден");
        }
        Item addedItem = itemDao.add(itemMapper.fromDto(itemDto));
        return itemMapper.toDto(addedItem);
    }

    public ItemDto patch(ItemDto itemDto) {
        long owner = itemDto.getOwner();
        if (!userService.isContains(itemDto.getOwner())) {
            throw new EntityNotFoundException("id владельца=" + owner + " предмета " + itemDto.getName() + " не найден");
        }
        if (!itemDao.isOwn(itemDto.getOwner(), itemDto.getId())) {
            throw new EntityNotFoundException(
                    "предмет " + itemDto.getName() + " id=" + itemDto.getId()
                            + " не владеет пользователь с id=" + itemDto.getOwner());
        }
        Item patchedItem = itemDao.patch(itemMapper.fromDto(itemDto));
        return itemMapper.toDto(patchedItem);
    }

    public ItemDto get(long id) {
        Item item = itemDao.get(id);
        if (!itemDao.isContains(id)) {
            throw new EntityNotFoundException("предмет c id=" + id + " не найден");
        }
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAll(long id) {
        if (!userService.isContains(id)) {
            throw new EntityNotFoundException("пользователь c id=" + id + " не найден");
        }
        List<Item> items = itemDao.getAll(id);
        if (items == null) return null;
        return itemDao.getAll(id).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    public List<ItemDto> findByParam(String text) {
        List<Item> items = itemDao.findByParam(text);
        return items.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }
}
