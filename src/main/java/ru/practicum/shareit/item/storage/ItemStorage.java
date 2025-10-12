package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item add(Item item);

    Item update(Item item);

    Item findById(Long itemId);

    Collection<Item> getUsersItems(Long userId);

    Collection<Item> findItemsOnText(String text);
}
