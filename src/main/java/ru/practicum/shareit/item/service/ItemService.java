package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.Collection;

@Service
public interface ItemService {

    ItemDtoOut add(Long userId, ItemDto itemDto);

    ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoOut findById(Long userId, Long itemId);

    Collection<ItemDtoOut> getUsersItems(Long userId);

    Collection<ItemDtoOut> findItemsOnText(String text);

    CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId);
}


