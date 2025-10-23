package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoOut add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto item) {
        log.info("Запрос на добавление вещи {}", item);
        return itemService.add(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable(name = "itemId") Long itemId,
                             @RequestBody ItemDto item) {
        log.info("Запрос на обновление вещи с id = {}", itemId);
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable(name = "itemId") Long itemId) {
        log.info("Запрос на поиск вещи по id = {}", itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDtoOut> getUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на поиск вещей для пользователя с id = {}", userId);
        return itemService.getUsersItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDtoOut> findItemsOnText(@RequestParam("text") String text) {
        log.info("Запрос на поиск вещей, содержащих строку = {}", text);
        return itemService.findItemsOnText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody CommentDto commentDto,
                                       @PathVariable(name = "itemId") Long itemId) {
        log.info("POST Запрос на создание комментария для вещи с id = {} от пользователя с id = {}", itemId, userId);
        return itemService.createComment(userId, commentDto, itemId);
    }
}
