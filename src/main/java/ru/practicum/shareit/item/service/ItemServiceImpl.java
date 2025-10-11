package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item add(Long userId, ItemDto item) {
        log.info("Запрос на добавление вещи {}", item.getName());

        if (userId == null) {
            String message = String.format("Запрос не может быть обработан, так как передан нулевой id пользователя");
            log.warn(message);
            throw new ValidationException(message);
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            String message = String.format("Запрос не может быть обработан, так как пользователь не найден в базе");
            log.warn(message);
            throw new NotFoundException(message);
        }

        Item newItem = itemStorage.add(user, item);
        if (newItem == null) log.warn("Ошибка при добавлении вещи {}", item.getName());
        log.info("Вещь {} успешно добавлена", item.getName());
        return newItem;
    }

    @Override
    public Item update(Long userId, Long itemId, ItemUpdDto item) {
        log.info("Запрос на обновление вещи с id = {}", itemId);

        if (userId == null) {
            String message = String.format("Запрос не может быть обработан, так как передан нулевой id пользователя");
            log.warn(message);
            throw new ValidationException(message);
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            String message = String.format("Запрос не может быть обработан, так как пользователь не найден в базе");
            log.warn(message);
            throw new NotFoundException(message);
        }

        Item updItem = itemStorage.findById(itemId);
        if (updItem == null) {
            String message = String.format("Вещь {} не найдена в базе данных", item.getName());
            log.warn(message);
            throw new NotFoundException(message);
        }

        if (!updItem.getOwner().getId().equals(userId)) {
            String message = String.format("Запрос не может быть обработан, так только хозяин вещи может её обновить");
            log.warn(message);
            throw new ValidationException(message);
        }

        updItem = itemStorage.update(itemId, item);
        log.info("Вещь {} успешно обновлёна", updItem.getName());
        return updItem;
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Запрос на поиск вещи по id = {}", itemId);
        Item item = itemStorage.findById(itemId);
        if (item == null) {
            String message = String.format("Вещь с id = %s не найдена", itemId);
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Вещь с id = {} найдена", itemId);
        return item;
    }

    @Override
    public Collection<ItemUsersDto> getUsersItems(Long userId) {
        log.info("Запрос на поиск вещей для пользователя с id = {}", userId);
        if (userId == null) {
            String message = String.format("Запрос не может быть обработан, так как передан нулевой id пользователя");
            log.warn(message);
            throw new ValidationException(message);
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            String message = String.format("Запрос не может быть обработан, так как пользователь не найден в базе");
            log.warn(message);
            throw new NotFoundException(message);
        }

        Collection<ItemUsersDto> items = itemStorage.getUsersItems(userId);
        if (items.isEmpty()) log.warn("Вещи у пользователя отсутствуют");
        log.info("Найдено {} вещей", items.size());
        return items;
    }

    @Override
    public Collection<Item> findItemsOnText(String text) {
        log.info("Запрос на поиск вещей, содержащих строку = {}", text);
        if (text.isBlank()) return new ArrayList<>();
        Collection<Item> items = itemStorage.findItemsOnText(text);
        if (items.isEmpty()) log.warn("Вещей соответствующих запрашиваемому описанию не найдено");
        log.info("Найдено {} вещей", items.size());
        return items;
    }
}
