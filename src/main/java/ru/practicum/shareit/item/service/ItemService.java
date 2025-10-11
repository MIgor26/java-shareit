package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;

import java.util.Collection;

@Service
public interface ItemService {
    ItemDto add(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemUpdDto item);

    ItemDto findById(Long itemId);

    Collection<ItemUsersDto> getUsersItems(Long userId);

    Collection<ItemDto> findItemsOnText(String text);
}
