package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.CreationException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

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
    public ItemDto add(Long userId, ItemDto item) {
        log.info("Запрос на добавление вещи {}", item.getName());

        if (userId == null) {
            String message = "Запрос не может быть обработан, так как передан нулевой id пользователя";
            throw new ValidationException(message);
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            String message = "Запрос не может быть обработан, так как пользователь не найден в базе";
            throw new NotFoundException(message);
        }

        Item newItem = itemStorage.add(ItemMapper.toItem(item, user));
        if (newItem == null) {
            String message = String.format("Ошибка при добавлении вещи %s", item.getName());
            throw new CreationException(message);
        }
        log.info("Вещь {} успешно добавлена", item.getName());
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdDto item) {
        log.info("Запрос на обновление вещи с id = {}", itemId);

        if (userId == null) {
            String message = "Запрос не может быть обработан, так как передан нулевой id пользователя";
            throw new ValidationException(message);
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            String message = "Запрос не может быть обработан, так как пользователь не найден в базе";
            throw new NotFoundException(message);
        }

        Item updItem = itemStorage.findById(itemId);
        if (updItem == null) {
            String message = String.format("Вещь %s не найдена в базе данных", item.getName());
            throw new NotFoundException(message);
        }

        if (!updItem.getOwner().getId().equals(userId)) {
            String message = "Запрос не может быть обработан, так только хозяин вещи может её обновить";
            throw new ValidationException(message);
        }

        // !! По сути это же маппинг? Сделал логику обновления в слое Service, чтобы в Storage передать сущность
        if (item.getName() != null) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updItem.setDescription(item.getDescription());
        }
        if ((Boolean) item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }

        itemStorage.update(updItem);
        log.info("Вещь {} успешно обновлёна", updItem.getName());
        return ItemMapper.toItemDto(updItem);
    }

    @Override
    public ItemDto findById(Long itemId) {
        log.info("Запрос на поиск вещи по id = {}", itemId);
        Item item = itemStorage.findById(itemId);
        if (item == null) {
            String message = String.format("Вещь с id = %s не найдена", itemId);
            throw new NotFoundException(message);
        }
        log.info("Вещь с id = {} найдена", itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemUsersDto> getUsersItems(Long userId) {
        log.info("Запрос на поиск вещей для пользователя с id = {}", userId);
        if (userId == null) {
            String message = "Запрос не может быть обработан, так как передан нулевой id пользователя";
            throw new ValidationException(message);
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            String message = "Запрос не может быть обработан, так как пользователь не найден в базе";
            throw new NotFoundException(message);
        }

        Collection<ItemUsersDto> items = itemStorage.getUsersItems(userId);
        if (items.isEmpty()) {
            log.warn("Вещи у пользователя отсутствуют");
        }
        log.info("У пользователя с id = {} найдено {} вещей", userId, items.size());
        return items;
    }

    @Override
    public Collection<ItemDto> findItemsOnText(String text) {
        log.info("Запрос на поиск вещей, содержащих строку = {}", text);
        if (text.isBlank()) return new ArrayList<>();
        Collection<Item> items = itemStorage.findItemsOnText(text.toLowerCase());
        if (items.isEmpty()) {
            log.warn("Вещей соответствующих запрашиваемому описанию не найдено");
            return new ArrayList<>();
        }
        log.info("Найдено {} вещей содержащих строку {}", items.size(), text);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
