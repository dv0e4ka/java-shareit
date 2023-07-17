package ru.practicum.shareit.request;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    User requester;
    User owner;
    ItemRequest itemRequestIn;
    ItemRequest itemRequestOut;
    ItemRequestDto itemRequestDtoIn;
    ItemRequestDto itemRequestDtoOut;
    List<ItemRequestDto> itemRequestDtoOutList;
    Item item;
    List<Item> itemList;

    ItemDto itemDto;
    long requesterId;
    LocalDateTime createdDateTime;

    @BeforeEach
    void setUp() {
        requester = User.builder().id(1L).name("requester").email("requester@mail.ru").build();
        requesterId = requester.getId();
        createdDateTime = LocalDateTime.of(2023, 7, 1, 0, 0);

        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@mail.ru")
                .build();


        itemRequestIn = ItemRequest.builder()
                .description("топор")
                .requester(requester)
                .build();

        itemRequestOut = ItemRequest.builder()
                .id(1L)
                .requester(requester)
                .description("топор")
                .created(createdDateTime)
                .build();

        itemRequestDtoIn = ItemRequestDto.builder()
                .description("топор")
                .build();

        itemRequestDtoOut = ItemRequestDto.builder()
                .id(1L)
                .requesterID(requesterId)
                .description("топор")
                .items(new ArrayList<>())
                .created(createdDateTime)
                .build();

        itemRequestDtoOutList = List.of(itemRequestDtoOut);

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner)
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .owner(owner.getId())
                .available(true)
                .build();

        itemList = List.of(item);
    }

    @Test
    void shouldAdd() {

        when(userRepository.findById(requesterId)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.save(itemRequestIn)).thenReturn(itemRequestOut);

        ItemRequestDto actual = itemRequestService.add(itemRequestDtoIn, 1L);

        Assertions.assertEquals(itemRequestDtoOut.getId(), actual.getId());
        Assertions.assertEquals(itemRequestDtoOut.getRequesterID(), actual.getRequesterID());
        Assertions.assertEquals(itemRequestDtoOut.getDescription(), actual.getDescription());

    }

    @Test
    void shouldAdd_whenUserNotFound_fail() {
        when(itemRequestRepository.save(itemRequestIn)).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService.add(itemRequestDtoIn, 999L));
    }

    @Test
    void findById() {
        ItemRequestDto expected = ItemRequestDto.builder()
                .id(1L)
                .description("топор")
                .requesterID(requesterId)
                .items(List.of(itemDto))
                .created(createdDateTime)
                .build();

        when(userRepository.findById(requesterId)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(itemRequestOut));
        when(itemRepository.findAllByRequestId(itemRequestOut.getId())).thenReturn(itemList);

        ItemRequestDto actual = itemRequestService.findById(1L, 1L);

        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected, actual);


    }

    @Test
    void findById_whenUserNotFound_fail() {
        when(userRepository.findById(999L)).thenThrow(new EntityNotFoundException(""));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(1L, 999L));
    }

    @Test
    void findById_whenRequestNotFound() {
        when(userRepository.findById(requesterId)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(999L)).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(999L, 1L));

    }

    @Test
    void findById_whenItemsNotFound() {
        when(userRepository.findById(requesterId)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(itemRequestOut));
        when(itemRepository.findAllByRequestId(itemRequestOut.getId())).thenThrow(new EntityNotFoundException(""));

        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(1L, 1L));

    }

    @Test
    void findAllByUser() {

        when(userRepository.findById(requesterId)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findByRequesterIdOrderById(1L)).thenReturn(List.of(itemRequestOut));

        Assertions.assertEquals(itemRequestDtoOut, itemRequestService.findAllByUser(1L).get(0));
    }

    @Test
    void findAllByUser_whenUserNotFound() {
        when(userRepository.findById(999L)).thenThrow(new EntityNotFoundException(""));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAllByUser(999L));
    }

    @Test
    void findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        PageRequest page = PageRequest.of(0,10, sort);

        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findAllByRequesterNot(owner, page)).thenReturn(new PageImpl<>(List.of(itemRequestOut)));

        Assertions.assertEquals(itemRequestDtoOut, itemRequestService.findAll(0, 10,2L).get(0));
    }

    @Test
    void findAll_whenUserNotFound() {
        when(userRepository.findById(999L)).thenThrow(new EntityNotFoundException(""));
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAll(0, 10, 999L));
    }
}