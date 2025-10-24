package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository,
                           BookingRepository bookingRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ItemDtoOut add(Long userId, ItemDto itemDto) {
        UserDto userDto = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner((UserMapper.toUser(userDto)));
        log.info("Вещь {} успешно добавлена", item);
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto) {
        UserDto userDto = userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                            return new NotFoundException("Вещь с " + itemId + " не найдена");
                        }
                );
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не является собственником вещи " + item);
        }
        Boolean isAvailable = itemDto.getAvailable();
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        String description = itemDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        String name = itemDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        itemRepository.save(item);
        log.info("Вещь {} успешно обновлёна", item.getName());
        return ItemMapper.toItemDtoOut(item);
    }

    @Override
    public ItemDtoOut findById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                            return new NotFoundException("Вещь с id = " + itemId + " не найдена");
                        }
                );
        log.info("Вещь с id = {} найдена", itemId);
        // Получение комментариев для любого пользователя
        List<CommentDtoOut> comments = getAllItemComments(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDtoOut(item, null, null, comments);
        }
        // Получение бронирований только для хозяина вещи
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, BookingStatus.APPROVED);
        List<BookingDtoOut> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(Collectors.toList());
        BookingDtoOut lastBooking = getLastBooking(bookingDTOList, LocalDateTime.now());
        BookingDtoOut nextBooking = getNextBooking(bookingDTOList, LocalDateTime.now());
        return ItemMapper.toItemDtoOut(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoOut> getUsersItems(Long userId) {
        UserDto owner = userService.findById(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        if (itemList.isEmpty()) {
            log.warn("Вещи у пользователя отсутствуют");
        }
        List<Long> itemIdList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, List<CommentDtoOut>> comments = commentRepository.findAllByItemIdIn(itemIdList)
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));

        Map<Long, List<BookingDtoOut>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(groupingBy(BookingDtoOut::getItemId, toList()));
        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoOut(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId())

                ))
                .collect(toList());
    }

    @Override
    public Collection<ItemDtoOut> findItemsOnText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Collection<Item> itemList = itemRepository.search(text);
        return itemList.stream()
                .filter(item -> item.getAvailable())
                .map(ItemMapper::toItemDtoOut)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        // Проверка, что пользователь брал вещь в аренду и аренда завершилась
        List<Booking> pastBookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now());

        // Исключение отклонённых бронирований
        pastBookings = pastBookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                .collect(Collectors.toList());

        if (pastBookings.isEmpty()) {
            throw new ValidationException("У пользователя с id = " + userId
                    + " должно быть хотя бы одно бронирование предмета с id = " + itemId);
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        commentRepository.save(comment);
        return CommentMapper.toCommentDtoOut(comment);
    }

    private List<CommentDtoOut> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }

    private BookingDtoOut getLastBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(b -> b.getStart().isBefore(time))
                .reduce((b1, b2) -> b1.getStart().isAfter(b2.getStart()) ? b1 : b2)
                .orElse(null);
    }

    private BookingDtoOut getNextBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings
                .stream()
                .filter(b -> b.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}
