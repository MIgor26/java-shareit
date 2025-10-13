package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdDto;
import ru.practicum.shareit.item.dto.ItemUsersDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto item) {
        return itemService.add(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable(name = "itemId") Long itemId,
                          @Valid @RequestBody ItemUpdDto item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable(name = "itemId") Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public Collection<ItemUsersDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUsersItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsOnText(@RequestParam("text") String text) {
        return itemService.findItemsOnText(text);
    }
}
