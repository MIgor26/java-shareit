package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDtoOut add(Long userId, BookingDto bookingDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        bookingValidation(bookingDto, user, item);
        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        booking = bookingRepository.save(booking);
        log.info("Запрос на создание бронирования {} создан", booking);
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + "не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец вещи имеет право изменять статус бронирования");
        }
        if (booking.getStatus().equals(BookingStatus.CANCELED)) {
            throw new ValidationException("Запрос на бронь был отменён и имеет статус CANCELED");
        }
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    public BookingDtoOut findBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не владелeц и не автор бронирования ");
        }
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    public List<BookingDtoOut> findByBooker(Long bookerId, String state, int from, int size) {
        User user = UserMapper.toUser(userService.findById(bookerId));
        List<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        int page = getPageNumber(from, size);
        Pageable pageable = PageRequest.of(page, size);

        switch (validState(state)) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, time, time, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, time, pageable);
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state);
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, status, pageable);
                break;
            default: // ALL
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingOut)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> findByOwner(Long ownerId, String state, int from, int size) {
        User user = UserMapper.toUser(userService.findById(ownerId));
        List<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        int page = getPageNumber(from, size);
        Pageable pageable = PageRequest.of(page, size);

        switch (validState(state)) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, time, time, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, time, pageable);
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state);
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status, pageable);
                break;
            default: // ALL
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingOut)
                .collect(Collectors.toList());
    }

    private void bookingValidation(BookingDto bookingDto, User user, Item item) {
        if (user.getId().equals(item.getOwner().getId())) {
            throw new ValidationException("Вещь не может быть забронирована самим собой");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }
    }

    private BookingState validState(String bookingState) {
        BookingState state = BookingState.from(bookingState);
        if (state == null) {
            throw new IllegalArgumentException("Неизвестный статус: " + bookingState);
        }
        return state;
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
