package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto add(ItemDto itemDto) {
        long ownerId = itemDto.getOwner();
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "id владельца=" + ownerId + " предмета " + itemDto.getName() + " не найден")
                );
        Item itemToSave = itemMapper.toItem(itemDto, user);
        Item itemAdded = itemRepository.save(itemToSave);
        return itemMapper.toDto(itemAdded);
    }

    @Override
    public ItemDto patch(ItemDto itemDto) {
        long ownerId = itemDto.getOwner();
        final Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "id предмета " + itemDto.getId() + " не найден"
                        )
                );
        if (item.getOwner().getId() != ownerId) {
            throw new EntityNotFoundException(
                    "предмет " + itemDto.getName() + " id=" + itemDto.getId()
                            + " не владеет пользователь с id=" + itemDto.getOwner());
        } else {

        }
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "id владельца=" + ownerId + " предмета " + itemDto.getName() + " не найден"
                        )
                );

        final Optional<String> itemDtoName = Optional.ofNullable(itemDto.getName());
        final Optional<String> itemDtoDescription = Optional.ofNullable(itemDto.getDescription());
        final Optional<Boolean> itemDtoAvailable = Optional.ofNullable(itemDto.getAvailable());

        itemDtoName.ifPresent(name -> {
            if (!name.isBlank()) {
                item.setName(name);
            }
        });

        itemDtoDescription.ifPresent(description -> {
            if (!description.isBlank()) {
                item.setDescription(description);
            }
        });

        itemDtoAvailable.ifPresent(available -> item.setAvailable(available));

        Item itemAdded = itemRepository.save(item);
        return itemMapper.toDto(itemAdded);
    }

    @Override
    public ItemDto findById(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                        "id предмета " + id + " не найден"
                )
        );
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findAllByUserId(long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException(
                        "id владельца=" + ownerId + " не найден"
                )
        );
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByParam(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCase(text.toLowerCase()
                        , text.toLowerCase()).stream()
                .filter(item -> item.isAvailable()).collect(Collectors.toList());
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
//        return null;
    }

    @Override
    public void deleteById(long id) {
        itemRepository.deleteById(id);
    }
}
