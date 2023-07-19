package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemOutDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingCommentInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    CommentRepository commentRepository;

    ItemDto itemDto;

    ItemDto itemDtoExpected;
    Item item;
    Item itemReturned;
    ItemWithBookingCommentInfoDto itemBookingCommentDto;
    User owner;
    User booker;
    Comment comment;
    CommentDto commentDto;
    List<CommentDto> commentDtoList;
    Booking lastBooking;
    Booking nextBooking;
    List<Booking> lastBookingList;
    List<Booking> nextBookingList;
    LocalDateTime startLastBookingDate;
    LocalDateTime endLastBookingDate;
    LocalDateTime startNextBookingDate;
    LocalDateTime endNextBookingDate;
    BookingForItemOutDto lastBookingForItemOutDto;
    BookingForItemOutDto nextBookingForItemOutDto;

    @BeforeEach
    void setUp() {
        startLastBookingDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        endLastBookingDate = LocalDateTime.of(2001, 1, 1, 0, 0);
        startNextBookingDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        endNextBookingDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.ru")
                .build();

        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner.getId())
                .build();

        itemDtoExpected = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner.getId())
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();

        itemReturned = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();
        itemBookingCommentDto = ItemWithBookingCommentInfoDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner.getId())
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .user(booker)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName(booker.getName())
                .created(LocalDateTime.of(2023, 6, 1, 0, 0))
                .build();

        commentDtoList = List.of(commentDto);

        lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2000, 1, 1, 0, 0))
                .end(LocalDateTime.of(2001, 1, 1, 0, 0))
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2024, 1, 1, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 0, 0))
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        lastBookingList = List.of(lastBooking);
        nextBookingList = List.of(nextBooking);

        lastBookingForItemOutDto = BookingForItemOutDto.builder()
                .id(1L)
                .start(startLastBookingDate)
                .end(endLastBookingDate)
                .bookerId(2L)
                .status(BookingStatus.APPROVED)
                .build();

        nextBookingForItemOutDto = BookingForItemOutDto.builder()
                .id(2L)
                .start(startNextBookingDate)
                .end(endNextBookingDate)
                .bookerId(2L)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void shouldAdd() {

        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(item)).thenReturn(itemReturned);

        ItemDto itemSaved = itemService.add(itemDto);

        Assertions.assertEquals(itemDtoExpected, itemSaved);
    }


    @Test
    void shouldAdd_whenNotOwner_fail() {
        itemDto.setOwner(999L);

        when(userRepository.findById(999L)).thenThrow(new EntityNotFoundException(""));

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.add(itemDto));
    }


    @Test
    void shouldAdd_whenNotFoundRequest_fail() {

        itemDto.setRequestId(999L);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findById(999L)).thenThrow(new EntityNotFoundException(""));

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemService.add(itemDto));
    }

    @Test
    void shouldPatch() {
        String newName = "new name";
        String newDescription = "new description";

        itemDto.setId(1L);
        itemDto.setName(newName);
        itemDto.setDescription(newDescription);
        itemDto.setAvailable(false);

        Item itemToPatch = Item.builder()
                .id(1L)
                .name(newName)
                .description(newDescription)
                .owner(owner)
                .available(false).build();
        ItemDto itemDtoExpected = ItemDto.builder()
                .id(1L)
                .name(newName)
                .description(newDescription)
                .owner(owner.getId())
                .available(false).build();


        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(itemToPatch));
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(itemToPatch)).thenReturn(itemToPatch);

        ItemDto actualItemDto = itemService.patch(itemDto);

        Assertions.assertEquals(itemDtoExpected, actualItemDto);
    }

    @Test
    void shouldPatch_whenItemNotFound_fail() {
        String newName = "new name";
        String newDescription = "new description";

        itemDto.setId(1L);
        itemDto.setName(newName);
        itemDto.setDescription(newDescription);
        itemDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.patch(itemDto));
    }

    @Test
    void shouldPatch_whenNotOwnerNotFound_fail() {
        String newName = "new name";
        String newDescription = "new description";

        itemDto.setId(1L);
        itemDto.setName(newName);
        itemDto.setDescription(newDescription);
        itemDto.setAvailable(false);

        Item itemToPatch = Item.builder()
                .id(1L)
                .name(newName)
                .description(newDescription)
                .owner(owner)
                .available(false).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(itemToPatch));
        when(userRepository.findById(1L)).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.patch(itemDto));
    }

    @Test
    void shouldPatch_whenNotOwner_fail() {
        String newName = "new name";
        String newDescription = "new description";

        itemDto.setId(1L);
        itemDto.setName(newName);
        itemDto.setDescription(newDescription);
        itemDto.setAvailable(false);

        owner.setId(999L);

        Item itemToPatch = Item.builder()
                .id(1L)
                .name(newName)
                .description(newDescription)
                .owner(owner)
                .available(false).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(itemToPatch));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.patch(itemDto));
    }

    @Test
    void findById_whenOwner() {
        itemDto.setId(1L);
        item.setId(1L);
        comment.setItem(item);

        itemBookingCommentDto.setLastBooking(lastBookingForItemOutDto);
        itemBookingCommentDto.setNextBooking(nextBookingForItemOutDto);
        itemBookingCommentDto.setComments(commentDtoList);


        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findFirstByItemIdAndStartBeforeOrderByEndDesc(eq(itemDto.getId()), any(LocalDateTime.class)))
                .thenReturn(lastBookingList);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(
                eq(itemDto.getId()), any(LocalDateTime.class), eq(BookingStatus.REJECTED)))
                .thenReturn(nextBookingList);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemWithBookingCommentInfoDto actual = itemService.findById(1L, 1L);

        Assertions.assertEquals(itemBookingCommentDto.getId(), actual.getId());
        Assertions.assertEquals(itemBookingCommentDto.getName(), actual.getName());
        Assertions.assertEquals(itemBookingCommentDto.getDescription(), actual.getDescription());
        Assertions.assertEquals(itemBookingCommentDto.getOwner(), actual.getOwner());
        Assertions.assertEquals(itemBookingCommentDto.getAvailable(), actual.getAvailable());
        Assertions.assertEquals(itemBookingCommentDto.getLastBooking(), actual.getLastBooking());
        Assertions.assertEquals(itemBookingCommentDto.getNextBooking(), actual.getNextBooking());
        Assertions.assertEquals(itemBookingCommentDto.getComments().size(), actual.getComments().size());
        Assertions.assertEquals(itemBookingCommentDto.getComments().get(0).getId(), actual.getComments().get(0).getId());
        Assertions.assertEquals(
                itemBookingCommentDto.getComments().get(0).getText(), actual.getComments().get(0).getText());
        Assertions.assertEquals(
                itemBookingCommentDto.getComments().get(0).getAuthorName(), actual.getComments().get(0).getAuthorName());
    }

    @Test
    void findById_whenNotOwner() {
        itemDto.setId(1L);
        item.setId(1L);
        comment.setItem(item);


        itemBookingCommentDto.setLastBooking(lastBookingForItemOutDto);
        itemBookingCommentDto.setNextBooking(nextBookingForItemOutDto);
        itemBookingCommentDto.setComments(commentDtoList);


        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        verify(bookingRepository, never()).findFirstByItemIdAndStartBeforeOrderByEndDesc(
                eq(itemDto.getId()), any(LocalDateTime.class));

        verify(bookingRepository, never()).findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(
                eq(itemDto.getId()), any(LocalDateTime.class), eq(BookingStatus.REJECTED));

        ItemWithBookingCommentInfoDto actual = itemService.findById(1L, 1L);

        Assertions.assertEquals(itemBookingCommentDto.getId(), actual.getId());
        Assertions.assertEquals(itemBookingCommentDto.getName(), actual.getName());
        Assertions.assertEquals(itemBookingCommentDto.getDescription(), actual.getDescription());
        Assertions.assertEquals(itemBookingCommentDto.getOwner(), actual.getOwner());
        Assertions.assertEquals(itemBookingCommentDto.getAvailable(), actual.getAvailable());

        Assertions.assertEquals(itemBookingCommentDto.getComments().size(), actual.getComments().size());
        Assertions.assertEquals(itemBookingCommentDto.getComments().get(0).getId(), actual.getComments().get(0).getId());
        Assertions.assertEquals(
                itemBookingCommentDto.getComments().get(0).getText(), actual.getComments().get(0).getText());
        Assertions.assertEquals(
                itemBookingCommentDto.getComments().get(0).getAuthorName(), actual.getComments().get(0).getAuthorName());
    }

    @Test
    void findById_whenItemNotFound_fail() {
        when(itemRepository.findById(1L)).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.findById(1L, 1L));
    }

    @Test
    void findAllByOwnerId() {
        itemDto.setId(1L);
        item.setId(1L);
        comment.setItem(item);
        List<ItemWithBookingCommentInfoDto> itemBookingCommentDtoList = List.of(itemBookingCommentDto);

        itemBookingCommentDto.setLastBooking(lastBookingForItemOutDto);
        itemBookingCommentDto.setNextBooking(nextBookingForItemOutDto);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findByOwnerIdOrderById(owner.getId())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of(lastBooking, nextBooking));

        Assertions.assertEquals(itemBookingCommentDtoList, itemService.findAllByOwnerId(1L));
    }

    @Test
    void findAllByOwnerId_whenOwnerNotFind() {
        when(userRepository.findById(owner.getId())).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemService.findAllByOwnerId(1L));
    }

    @Test
    void findByParam() {
        List<ItemDto> itemDtoExpectedList = List.of(itemDto);
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase(anyString(), anyString())).thenReturn(List.of(item));

        Assertions.assertEquals(itemDtoExpectedList, itemService.findByParam("text"));
    }

    @Test
    void findByParam_whenParamIsBlank() {
        List<ItemDto> itemDtoExpectedList = Collections.emptyList();
        Assertions.assertEquals(itemDtoExpectedList, itemService.findByParam(""));
    }


    @Test
    void shouldAddComment() {
        CommentDto commentDtoRequest = CommentDto.builder().text("comment").build();
        Comment commentRequest = Comment.builder().user(booker).item(item).text("comment").build();
        Comment commentResponse = Comment.builder().id(1L).user(booker).item(item).text("comment").build();

        CommentDto commentDtoExpected = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName(booker.getName())
                .build();


        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(commentRepository.save(commentRequest)).thenReturn(commentResponse);

        CommentDto actual = itemService.saveComment(commentDtoRequest, 2L, 2L);

        Assertions.assertEquals(commentDtoExpected.getId(), actual.getId());
        Assertions.assertEquals(commentDtoExpected.getAuthorName(), actual.getAuthorName());
        Assertions.assertEquals(commentDtoExpected.getText(), actual.getText());

    }

    @Test
    void shouldSaveComment_whenBookingNotFound_fail() {
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);

        Assertions.assertThrows(ValidationException.class, () -> itemService.saveComment(commentDto, 2L, 1L));
    }

}