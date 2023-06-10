package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemDaoInMemory implements ItemDao {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private final Map<Long, List<Long>> ownerMap = new HashMap<>();
    private long idCounter = 0;

    @Override
    public Item add(Item item) {
        long itemId = item.setId(++idCounter);

        long ownerId = item.getOwner();
        List<Long> itemsList = new ArrayList<>();
        if (ownerMap.get(ownerId) != null) {
            itemsList = ownerMap.get(ownerId);
        }
        itemsList.add(itemId);

        ownerMap.put(ownerId, itemsList);

        itemMap.put(itemId, item);
        return itemMap.get(itemId);
    }

    @Override
    public Item patch(Item item) {
        Item oldItem = itemMap.get(item.getId());
        if (item.getName() == null) item.setName(oldItem.getName());
        if (item.getDescription() == null) item.setDescription(oldItem.getDescription());
        if (item.getAvailable() == null) item.setAvailable(oldItem.getAvailable());
        itemMap.put(item.getId(), item);
        return itemMap.get(item.getId());
    }

    @Override
    public List<Item> getAll(long id) {
        if (ownerMap.get(id) == null) return null;
        return ownerMap.get(id).stream().map(itemMap::get).collect(Collectors.toList());
    }

    @Override
    public Item get(long id) {
        return itemMap.get(id);
    }

    @Override
    public boolean isContains(long id) {
        return itemMap.containsKey(id);
    }

    @Override
    public boolean isOwn(long ownerId, long itemId) {
        if (ownerMap.containsKey(ownerId)) {
            return ownerMap.get(ownerId).stream().anyMatch(id -> id == itemId);
        } else {
            return false;
        }
    }

    @Override
    public List<Item> findByParam(String text) {
        // TODO:
        List<Item> items = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getName().contains(text) || item.getDescription().contains(text)) {
                items.add(item);
            }
        }
        return items;
    }
}
