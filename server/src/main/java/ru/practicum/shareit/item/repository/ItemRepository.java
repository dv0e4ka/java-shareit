package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderById(long owner);

    List<Item> findByNameOrDescriptionContainingIgnoreCase(String name, String description);

    List<Item> findAllByRequestId(long id);

    List<Item> findAllByRequestIdIn(List<Long> id);
}
