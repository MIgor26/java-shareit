package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    // !! По сути метод можно сделать void. Я не знаю как правильно. Может тип Item нужно на будущее?
    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getUsersItems(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findItemsOnText(String text) {
        return items.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
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
