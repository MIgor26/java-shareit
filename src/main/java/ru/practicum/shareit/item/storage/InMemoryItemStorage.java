package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(User user, ItemDto item) {
        Item newItem = Item.builder()
                .id(getNextId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(user)
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item update(Long itemId, ItemUpdDto item) {
        Item updItem = items.get(itemId);
        if (item.getName() != null) {
            updItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updItem.setDescription(item.getDescription());
        }
        if ((Boolean) item.getAvailable() != null) {
            updItem.setAvailable(item.getAvailable());
        }
        return updItem;
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<ItemUsersDto> getUsersItems(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(item -> ItemMapper.toItemUsersDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findItemsOnText(String text) {
        System.out.println("Запуск работы Сторэйдж и поиск строки = " + text);
        return items.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    private long getNextId() {
        int currentMaxId = items.keySet()
                .stream()
                .mapToInt(id -> Math.toIntExact(id))
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
