package ru.practicum.shareit.item;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
//    Item add(Item item, Long owner);
    Item add(Item item);

    Item patch(Item item);

    boolean isOwn(long ownerId, long itemId);

    Item get(long id);

    List<Item> getAll(long id);

    boolean isContains(long id);

    List<Item> findByParam(String text);

    List<Item> getAllAddedItems();
}
