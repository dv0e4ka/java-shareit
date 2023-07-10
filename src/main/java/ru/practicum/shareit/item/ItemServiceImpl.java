package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingCommentInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto add(ItemDto itemDto) {
        long ownerId = itemDto.getOwner();
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "id владельца=" + ownerId + " предмета " + itemDto.getName() + " не найден")
                );
        Item itemToSave = ItemMapper.toItem(itemDto, user);
        Item itemAdded = itemRepository.save(itemToSave);
        return ItemMapper.toDto(itemAdded);
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
        return ItemMapper.toDto(itemAdded);
    }

    @Override
    public ItemWithBookingCommentInfoDto findById(long itemId, long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(
                        "id предмета " + itemId + " не найден"
                )
        );
        ItemWithBookingCommentInfoDto itemDto = ItemMapper.toItemWithBookingInfoDto(item);
        if (item.getOwner().getId() == ownerId) {
            addDate(itemDto, LocalDateTime.now());

        }
        itemDto = addComment(itemDto, Optional.of(itemId), Optional.empty());
        return itemDto;
    }

    @Override
    public List<ItemWithBookingCommentInfoDto> findAllByOwnerId(long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException(
                        "id владельца=" + ownerId + " не найден"
                )
        );
        List<ItemWithBookingCommentInfoDto> itemsDto = itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemWithBookingInfoDto)
                .map(item -> addComment(item, Optional.empty(), Optional.of(item.getOwner())))
                .collect(Collectors.toList());
        itemsDto.forEach(item -> addDate(item, LocalDateTime.now()));
        itemsDto.sort(Comparator.comparing(ItemWithBookingCommentInfoDto::getId));
//        itemsDto.forEach(item -> );
        return itemsDto;
    }

    private void addDate(ItemWithBookingCommentInfoDto itemDto, LocalDateTime now) {
        List<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByEndDesc(itemDto.getId(), now);
        if (!lastBooking.isEmpty()) {
            itemDto.setLastBooking(BookingMapper.toBookingForItemOutDto(lastBooking.get(0)));
        }
        List<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(
                itemDto.getId(), now, BookingStatus.REJECTED
        );
        if (!nextBooking.isEmpty()) {
            itemDto.setNextBooking(BookingMapper.toBookingForItemOutDto(nextBooking.get(0)));
        }
    }

    @Override
    public List<ItemDto> findByParam(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCase(text.toLowerCase(),
                        text.toLowerCase()).stream()
                .filter(Item::isAvailable).collect(Collectors.toList());
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto, long userId, long itemId) {
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
        if (booking == null) {
            throw new ValidationException("no comment exception");
        }
        Comment comment = CommentMapper.toComment(commentDto,  booking.getBooker(), booking.getItem());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private ItemWithBookingCommentInfoDto addComment(
            ItemWithBookingCommentInfoDto item, Optional<Long> itemId, Optional<Long> userId) {


        itemId.ifPresent(id -> {
            List<Comment> commentList = commentRepository.findAllByItemId(id);
            item.setComments(commentList.stream().map(CommentMapper::toDto).collect(Collectors.toList()));
        });

        userId.ifPresent(id -> {
            List<Comment> commentList = commentRepository.findAllByUserId(id);
            item.setComments(commentList.stream().map(CommentMapper::toDto).collect(Collectors.toList()));
        });
        return item;
    }
}
