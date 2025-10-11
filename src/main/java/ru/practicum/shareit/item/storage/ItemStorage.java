package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorage {
    Item add(User user, ItemDto item);

    Item update(Long itemId, ItemUpdDto item);

    Item findById(Long itemId);

    Collection<ItemUsersDto> getUsersItems(Long userId);

    Collection<Item> findItemsOnText(String text);
}
