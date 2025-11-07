package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final User owner = User.builder()
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
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.APPROVED)
            .item(item)
            .booker(user)
            .build();

    private final Booking bookingWaiting = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(user)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingDto bookingDtoEndBeforeStart = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    @Test
    void create() {
        BookingDtoOut expectedBookingDtoOut = BookingMapper.toBookingOut(BookingMapper.toBooking(user, item, bookingDto));
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.toBooking(user, item, bookingDto));

        BookingDtoOut actualBookingDtoOut = bookingService.add(userDto.getId(), bookingDto);

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void createWhenEndIsBeforeStart() {
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), bookingDtoEndBeforeStart));

        assertEquals(bookingValidationException.getMessage(), "Дата окончания не может быть раньше или равна дате начала");
    }

    @Test
    void createWhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(bookingValidationException.getMessage(), "Вещь не доступна для бронирования.");
    }

    @Test
    void createWhenItemOwnerEqualsBooker() {
        item.setOwner(user);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(validationException.getMessage(), "Вещь не может быть забронирована самим собой");
    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoOut actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(BookingStatus.APPROVED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateWhenStatusNotApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoOut actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), false);

        assertEquals(BookingStatus.REJECTED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateWhenUserIsNotItemOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> bookingService.update(userDto.getId(), booking.getId(), true));

        assertEquals(accessDeniedException.getMessage(), "Только владелец вещи имеет право изменять статус бронирования");
    }

    @Test
    void getById() {
        BookingDtoOut expectedBookingDtoOut = BookingMapper.toBookingOut(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoOut actualBookingDtoOut = bookingService.findBooking(user.getId(), booking.getId());

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void getByIdWhenBookingIdIsNotValid() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findBooking(1L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Бронирование не найдено");
    }

    @Test
    void getByIdWhenUserIsNotItemOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findBooking(3L, booking.getId()));

        assertEquals(notFoundException.getMessage(), "Пользователь не владелeц и не автор бронирования ");
    }

    @Test
    void getAllByBookerWhenBookingStateAll() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByBooker(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateCURRENT() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByBooker(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStatePAST() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByBooker(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateFUTURE() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByBooker(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateWAITING() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByBooker(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateREJECTED() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByBooker(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateAll() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByOwner(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateCURRENT() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByOwner(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStatePAST() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByOwner(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateFUTURE() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByOwner(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateWAITING() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByOwner(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateREJECTED() {
        List<BookingDtoOut> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookingsDtoOut = bookingService.findByOwner(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);

    }

    @Test
    void getAllByOwnerWhenBookingStateIsNotValid() {
        when(userService.findById(user.getId())).thenReturn(userDto);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findByOwner(user.getId(), "ERROR", 0, 10));
    }
}