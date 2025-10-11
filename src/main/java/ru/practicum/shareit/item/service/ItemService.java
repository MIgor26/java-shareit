package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Service
public interface ItemService {
    Item add(Long userId, ItemDto item);

    Item update(Long userId, Long itemId, ItemUpdDto item);

    Item findById(Long itemId);

    Collection<ItemUsersDto> getUsersItems(Long userId);

    Collection<Item> findItemsOnText(String text);
}
