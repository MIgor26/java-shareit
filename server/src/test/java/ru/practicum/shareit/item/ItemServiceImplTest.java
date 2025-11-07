package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("username2")
            .email("email2@email.com")
            .build();


    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemDtoOut itemDto = ItemDtoOut.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .comments(Collections.emptyList())
            .build();

    private final ItemDto itemDtoUpdate = ItemDto.builder()
            .build();

    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking lastBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .id(3L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(10L))
            .end(LocalDateTime.now().minusDays(9L))
            .build();

    private final Booking nextBooking = Booking.builder()
            .id(4L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .id(5L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(10L))
            .end(LocalDateTime.now().plusDays(20L))
            .build();

    @Test
    void getItemById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemService.findById(user.getId(), item.getId());

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void updateItem() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userService.findById(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));

        ItemDtoOut savedItem = itemService.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(updatedItem));

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void updateItemWhenUserIsNotItemOwner() {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("item name")
                .description("updated description")
                .available(false)
                .owner(user2)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(updatedItem));
        when(userService.findById(user.getId())).thenReturn(userDto);

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(updatedItem)));

        assertEquals(itemNotFoundException.getMessage(), "Пользователь с id = " + user.getId() +
                " не является собственником вещи " + item);
    }

    @Test
    void updateItemWhenItemIdIsNotValid() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(item)));
        assertEquals(itemNotFoundException.getMessage(), "Вещь с " + item.getId() + " не найдена");
    }

    @Test
    void getItemsById() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findAllByOwnerId(user.getId(), pageable)).thenReturn(List.of(item));

        List<ItemDtoOut> listItem = itemService.getUsersItems(user.getId(), 0, 10);

        assertEquals(1, listItem.size());
        assertEquals(item.getName(), listItem.getFirst().getName());
    }

    @Test
    void createComment() {
        CommentDtoOut expectedCommentDto = CommentMapper.toCommentDtoOut(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoOut actualCommentDto = itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId());

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void findByText() {
        String text = "desc";
        when(itemRepository.search(text)).thenReturn(List.of(item));
        List<ItemDtoOut> itemList = (List<ItemDtoOut>) itemService.findItemsOnText(text);

        assertEquals(itemList.get(0), ItemMapper.toItemDtoOut(item));
    }

    @Test
    void createComment_whenItemIdIsNotValid_thenThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(itemNotFoundException.getMessage(), "Вещь с id = " + item.getId() + " не найдена");
    }

    @Test
    void createCommentWhenUserHaveNotAnyBookingsShouldThrowValidationException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException userBookingsNotFoundException = assertThrows(ValidationException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(userBookingsNotFoundException.getMessage(), "У пользователя с id = " + user.getId() + " должно быть хотя бы одно бронирование предмета с id = " + item.getId());

    }
}