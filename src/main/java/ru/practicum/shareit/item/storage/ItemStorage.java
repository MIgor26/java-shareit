package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item add(Item item);

    Item update(Item item);

    Item findById(Long itemId);

    Collection<ItemUsersDto> getUsersItems(Long userId);

    Collection<Item> findItemsOnText(String text);
}
